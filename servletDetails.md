# Login
```
{
  name: Login.java,
  method: POST,
  values: {
    username,
    password
  },
  return: {
    success: loginSuccess.html,
    error: index.html#errorLog
  }
}
```
# Register
```
{
  name: Register.java,
  method: POST,
  values: {
    username,
    email,
    type,
    password,
    passwordConf
  },
  return: {
    success: registerSuccess.html,
    error: index.html#errorReg
  }
}
```
