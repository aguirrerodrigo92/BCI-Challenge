# BCI - Challenge

This project is a simple API to manage Users creation and login.

## Installation
Download the code from the repository and execute the following command from the root folder

```bash
gradlew run
```

You can also build a jar file with the application
```bash
gradlew build
```
After it finishes you will find it under build \ libs. To run it execute
```bash
java -jar {file name}
```

## REST API
By default, the application run in the port 8080.

- All password must follow the format of one uppercase letter, lowercase case letters and two digits

- All email must follow the format of smth@smth.smth


#### Create User

#### Request
`POST /api/users/signup`

```
{
    "name" : "John Doe",
    "email": "johndoe@example.com",
    "password": "a2asfGfdfdf4",
    "phones": [
        {
            "number": 123123123,
			"city_code": 11,
			"country_code": "+54"
        }
    ]
}
```

#### Response
`Status 201 Created`
```
{
    "id": "3fc60002-8134-41cd-8ea4-213de3cfc9d2",
    "created": "2023-11-30T10:20:58.2560806",
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyb2RyaWdvLmFndWlycmUyQGdsb2JhbGxvZ2ljLmNvbSIsImlhdCI6MTcwMTM1MDQ1OCwiZXhwIjoxNzAxMzUyMjU4fQ.Y-XDT4nB6Gl41y5N_ZZg_8-vUCwtM-835U42KJPtey4"
}
```

### Get User
#### Request
`POST /api/users/login`

```
{
    "email" : "johndoe@example.com",
    "password": "a2asfGfdfdf4"
}
```
#### Response
`Status 200 OK`
```
{
    "id": "3fc60002-8134-41cd-8ea4-213de3cfc9d2",
    "created": "nov. 30, 2023 10:20:58 a. m.",
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyb2RyaWdvLmFndWlycmUyQGdsb2JhbGxvZ2ljLmNvbSIsImlhdCI6MTcwMTM1MDY1NCwiZXhwIjoxNzAxMzUyNDU0fQ.EIkeEJNZUW6WWyQlp32pf9KL_62fCch3X6_8UHTnKYU",
    "name" : "John Doe",
    "email": "johndoe@example.com",
    "password": "$2a$10$6Qs6FAbg7.eXqAEvwqQeguqyeUmJ1PXtJUx6FEmVFjFTGc726Li5W",
    "phones": [
        {
            "number": 123123123,
            "city_code": 11,
            "country_code": "+54"
        }
    ],
    "last_login": "nov. 30, 2023 10:24:14 a. m.",
    "is_active": true
}
```