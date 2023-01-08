# Node.js server

for use this server:

1. run `npm install`
1. change the connction details in the .env file to your details
1. create new SHA-256 key for JWT tokens 
1. run `npm start`


### `.env` file example

```
NODE_ENV=development
PORT=3000

DB_HOST=localhost
DB_NAME=room8s
DB_USER=root
DB_PASSWORD=root


ACCESS_TOKEN_SECRET=25cb04e8410f077226291f64c5b6b7f1411d4921e23fea1d2a9c22a8675691add591263a16d3a275e42b797d5ffa0a21b509f139d954b1d911332245d7eee55b
REFRESH_TOKEN_SECRET=7ddefe5a3491e8748fd95da9f43342753bed8a5828924ee0b02fb58b453d82529bded92c8d4bc4cca6b2c70113ab7a12460853f788bec15fbee897dd51c5d513

ADMIN_MAIL=example@example.com
ADMIN_PASS=<YOUR_POSSWORD>
```
