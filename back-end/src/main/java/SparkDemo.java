
import static com.mongodb.client.model.Filters.*;
import static spark.Spark.*;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

//transaction collection
class TransactionDTO{
  String sendTo;
  String sendFrom;
  float amount;
  String paymentType;
  String note;

  public TransactionDTO(String sendTo, String sendFrom, float amount, String paymentType, String note) {
    this.sendTo = sendTo;
    this.sendFrom = sendFrom;
    this.amount = amount;
    this.paymentType = paymentType;
    this.note = note;
  }
}

class TransactionResponseDto{
Boolean isPaidIn;
String error;

  public TransactionResponseDto(Boolean isPaidIn, String error) {
    this.isPaidIn = isPaidIn;
    this.error = error;
  }
}

//user collection dto
class UserDto{
  String username;
  String password;
}

class LoginResponseDto{
  Boolean isLoggedIn;
  String error;

  public LoginResponseDto(Boolean isLoggedIn, String error) {
    this.isLoggedIn = isLoggedIn;
    this.error = error;
  }
}


public class SparkDemo {

  public static void main(String[] args) {
    port(1234);

    // open connection
    MongoClient mongoClient = new MongoClient("localhost", 27017);
    // get ref to database
    MongoDatabase db = mongoClient.getDatabase("UsersDatabase");
    // get ref to collection
    MongoCollection<Document> usersCollection = db.getCollection("usersCollection");
    // get ref to transactions collection
    MongoCollection<Document> transactionsCollection = db.getCollection("transactionsCollection");


    Gson gson = new Gson();

    //username endpoint
    post("/logIn", (req, res) -> {
      String body = req.body();
      UserDto loginDto = gson.fromJson(body, UserDto.class);
      System.out.println("body received"+ body);
      Document search = usersCollection.find(eq("username", loginDto.username)).first();
      if (search != null) {// find record where username is x
        System.out.println("user found");
        if (search.get("password").equals(loginDto.password)) {// if record is found, check if password is same as logindto password
          return gson.toJson(new LoginResponseDto(true, null));
        } else {
          return gson.toJson(new LoginResponseDto(false, "Invalid password"));
        }
      } else {
        //can't find user
        return gson.toJson(new LoginResponseDto(false, "User not found"));
      }
    });

    //Register endpoint
    post("/register", (req, res) -> {
      String body = req.body();
      UserDto userDto = gson.fromJson(body, UserDto.class);
      Document doc = new Document("username", userDto.username)
              .append("password", userDto.password);
      // insert document into collection
      usersCollection.insertOne(doc);
      return gson.toJson(new LoginResponseDto(true, null));
    });

    //Validating the users
    post("/validUser", (req, res) -> {
      // get the json body and extract the username
      // check the database for a user named such
      // to trace if he is logged in, check the Map of Users if he is in
      // if it exists, then he is logged in
      String body = req.body();
      UserDto userDto = gson.fromJson(body, UserDto.class);
      Document existingUser = usersCollection.find(eq("username", userDto.username)).first();
      if(existingUser != null){
        if(existingUser.getString("password").equals(userDto.password)){
          return gson.toJson(new LoginResponseDto(true, null));
        }
        else {
          return gson.toJson(new LoginResponseDto(false, "User does not exist"));
        }
      }
      else {
        return gson.toJson(new LoginResponseDto(false, "User does not exist"));
      }
    });

    //transaction endpoint
    post("/makePayment", (req, res) -> {
      // use the payment architecture already done
      String body = req.body();
      TransactionDTO transactionDTO = gson.fromJson(body, TransactionDTO.class);
      // insert into transactions collection
      Document doc = new Document("sendTo", transactionDTO.sendTo)
              .append("sendFrom", transactionDTO.sendFrom)
              .append("amount", transactionDTO.amount)
              .append("paymentType", transactionDTO.paymentType)
              .append("note", transactionDTO.note);
      // insert document into collection
      transactionsCollection.insertOne(doc);
      return gson.toJson(new TransactionResponseDto(true, null));
    });

    //get all the transactions and display them on the front end
    get("/getMessages", (req,res) -> {
      List<Document> messages = new ArrayList<>();
      List<Document> doc = transactionsCollection.find().into(new ArrayList<>());

      for(Document d : doc){ 
        messages.add(Document.parse(d.toJson()));
      }
      return gson.toJson(messages);
    });
  }
}