# Banking-System
The aim of this project is to build a banking system.
The system must have 4 types of accounts: StudentChecking, Checking, Savings, and CreditCard and  3 types of Users: Admins and AccountHolders.

![classDiagram](img/BankingSystemclasses.png)
![caseDiagram](img/CaseDiagramBankingSystem.png)

## Main Functionalities
- Create new users
- Create accounts
- Access balance
- Debit and credit balance
- Accounts are freeze if any fraud pattern is detected
- Interest and penalties are applied when requiered.

## Important Features
- Postman and Swagger can be used to test all the endpoints. 
- To run the program with swagger open http://localhost:8080/swagger-ui.html
- To run this project locally do the following after cloning the project:
 1. Create two databases: bankingSystem and bankingSystemTest.
 2. Run mvn spring-boot:run to launch the application.
 3. Use postman to test all the functionalities.

- Security is applied to most of the routes. 
1. *Admins should be able to access the balance for any account, to debit the balance, and to credit the balance. Admins can also create thrid party and account users, and, change status of a frozen account to active.*
2. *Third Party users can debit or credit accounts of any type. To do so the must provide their hashed key in the header of the HTTP request. They also must provide the amount, the Account id and the account secret key.*
3. *Account holders should be able to transfer money from any of their accounts to any other account, as well as, access their own account balance.*

## Tools
- Swagger
- MySQL
- Spring JPA for data persistence
