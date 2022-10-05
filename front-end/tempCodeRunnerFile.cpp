#include <iostream>
#include <string>
#include "vector"
#include <memory>

using namespace std;

const int y = 1;

int main() {

	int static y = 2;

	int i = 3, j = 4, m = 5, n = 6;

	int a = [](int x, int i = 1) { return x * i; } (y, 3);

	int b = [=](int x) { return [=](int b) { return b + j; }(x) % 7; }(a);

	int c = [=](int x) mutable ->int {

		m = 6;

		return [&](int j) mutable {
			y = a * b;
			return y / j;

		}(x)-m;

	}(b);

	cout << a << endl;
	cout << b << endl;
	cout << c << endl;
	cout << m << endl;
	cout << y << endl;

	return 0;
}
