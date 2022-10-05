import './App.css';
import React from 'react';

function App() {
  //states variables means they are variables that hold the value represent the data of the app
  const [username, setUsername] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [recipient, setRecipient] = React.useState('');
  const [sender, setSender] = React.useState('');
  const [amount, setAmount] = React.useState('');
  const [paymentType, setPaymentType] = React.useState('');
  const [note, setNote] = React.useState('');
  const [isLoggedIn, setIsLoggedIn] = React.useState(false);
  const [error, setError] = React.useState(null);
  const [messages,setMessages] = React.useState([]);//all messages, transactions

  const handleSubmit = () => {
    const body = {//data send to login endpoint of the Spark server
      username: username,
      password: password,
    };
    const settings = {//method to use to send the data, 'post' method
      method: 'post',
      body: JSON.stringify(body),//converting data into JASON
    };
    fetch('/logIn', settings)//makes call to Spark login endpoint
      .then(res => res.json())//get the response from Spark, then convert the response to JASON
      .then(data => {//data is extracted and check for login success or fail
        if (data.isLoggedIn) {
          setIsLoggedIn(true);//if loggedin, state variavle called isloggedin is set to true
        } else if (data.error) {
          setError(data.error);
        }
      })
      .catch(e => console.log(e));//e is error object, return error if there are any problems, printing the object into the console
  };

  function getMessages() {
    fetch('/getMessages')
        .then(res => res.json()) //built in json to js
        .then((data) => {
            console.log(data);
            setMessages(data);
        })
        .catch(error => console.log(error));
}

  const handleRegister = () => {
    const body = {
      username: username,
      password: password,
    };
    const settings = {
      method: 'post',
      body: JSON.stringify(body),
    };
    fetch('/register', settings)
      .then(res => res.json())//converting the response to JASON
      .then(data => {
        if (data.isLoggedIn) {
          // display landing page
          setIsLoggedIn(true);
        } else if (data.error) {
          setError(data.error);
        }
      })
      .catch(e => console.log(e));
  };


  const handlePayment = () => {
    const body = {//creating objects
      sendTo: recipient,
      sendFrom: sender,
      amount: amount,
      paymentType: paymentType,
      note: note
    };
    const settings = {
      method: 'post',
      body: JSON.stringify(body),
    };
    fetch('/makePayment', settings)
      .then(res => res.json())
      .then(data => {
        if (data.isPaidIn) {
          // display landing page
          //setIsLoggedIn(true);
          console.log("successful transaction")
        } else if (data.error) {
          setError(data.error);
        }
      })
      .then(()=> getMessages())
      .catch(e => console.log(e));
  };

if (isLoggedIn) {
      return (
        <div>
          <div>
          <h1>Welcome {username}!</h1>

          <div>Recipient name<input value={recipient} onChange={e => setRecipient(e.target.value)} /></div>
          <div>Sender name<input value={sender} onChange={e => setSender(e.target.value)} /></div>
          <div>Amount<input value={amount} onChange={e => setAmount(e.target.value)} /></div>
          <div>Payment type<input value={paymentType} onChange={e => setPaymentType(e.target.value)} /></div>
          <div>Note<input value={note} onChange={e => setNote(e.target.value)} /></div>
          <button onClick={handlePayment}>Make Payment</button>
          {error}
          </div>
          <div>
          {messages.map(m => { //display all the transaction
                            return (
                                <div id="transactionStyle">
                                    <label>Sender:  </label> {m.sendFrom} <br />
                                    <label>Recipient: </label> {m.sendTo} <br />
                                    <b>Payment of ${m.amount} in {m.paymentType}</b> <br />
                                    <b>Notes: </b>{m.note} <br/>
                                    {/* <label>USERNAME</label> {m.fr} <br /> */}
                                </div>
                            );
                        })}
                        </div>
      </div>
    );

  }

  return (
    <div>
      <div>Username<input value={username} onChange={e => setUsername(e.target.value)} /></div>
      <div>Password<input type="password" value={password} onChange={e => setPassword(e.target.value)} /></div>
      <button onClick={handleSubmit}>Submit</button> <button onClick={handleRegister}>Register</button>
      {error}
    </div >
  );
}

export default App;