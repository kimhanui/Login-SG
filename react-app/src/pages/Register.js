import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import Avatar from "@mui/material/Avatar";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Container from "@mui/material/Container";
import CssBaseline from "@mui/material/CssBaseline";
import Grid from "@mui/material/Grid";
import Link from "@mui/material/Link";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import axios from "axios";
import { SnackbarProvider, useSnackbar } from 'notistack';
import React, { useState } from "react";


const theme = createTheme();

function Register() {
  const [errorMsgEmail, setErrorMsgEmail] = useState("");
  const [errorMsgPw, setErrorMsgPw] = useState("");
  const [errorMsgUserName, setErrorMsgUserName] = useState("");
  const errorMsg = {
    EMAIL: "이메일 형식으로 적어주세요. (한글 x)",
    PW: "영어 알파벳, 숫자, 일부 특수기호만 입력해주세요. (4~12자리)",
    USERNAME: "10자리 이하로 입력해주세요."
  };
  let emailCheck = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/;
  let passwordCheck = /[0-9a-zA-Z!@#$%^&*]/;


  //////////// snackbar handling ////////////
  const { enqueueSnackbar } = useSnackbar();
  const handleClickVariant = (variant, msg) =>  {
    // variant could be success, error, warning, info, or default
    enqueueSnackbar(msg, {"variant":variant});
  };
  ///////////////////////////////////////////

  const validationCheck = (data) => {
    const email = data.get("email");
    const password = data.get("password");
    const username = data.get("username");

    let result = true;
    if (email === "" || !email.includes("@") || emailCheck.test(email)) {
      setErrorMsgEmail(errorMsg.EMAIL);
      result = false;
    } else setErrorMsgEmail("");

    if (
      password === "" ||
      password.length < 4 ||
      password.length > 12 ||
      !passwordCheck.test(password)
    ) {
      setErrorMsgPw(errorMsg.PW);
      result = false;
    } else setErrorMsgPw("");

    if(username === "" ||
    username.length >10){
      setErrorMsgUserName(errorMsg.USERNAME);
      result = false;
    } else setErrorMsgUserName("");

    return result;
  };


  const regitsterRequest = (data) => {
    axios
      .post("http://localhost:8080/user/register", {
        email: data.get("email"),
        password: data.get("password"),
        name:data.get("username"),
      },{
        responseType:"application/json"
      })
      .then(function (response) {
        console.log("signIn resp: " + JSON.stringify(response.data));
        handleClickVariant("success", "정상적으로 회원가입되었습니다.");
      })
      .catch(function (error) {
        const errorContent = error.response.data;
        console.log("signIn error: " + JSON.stringify(error.response));
        if (errorContent.status == "REQUEST_TIMEOUT") {
          console.log("refresh token으로 access 재발급");
          axios.post("http://localhost:8080/user/reissueactoken", {
            email:`${window.localStorage.getItem("email")}`,
            refreshToken:`${window.localStorage.getItem("refreshToken")}`
          },{
            responseType: "application/json",
          }).then(function (response) {
            console.log(JSON.stringify(response.data));
            window.localStorage.setItem("accessToken", response.data.accessToken);
            handleClickVariant(
              "info","인증을 연장했습니다. 다시 요청해주세요"
            );
          }).catch(function(error){
            handleClickVariant(
              "error",
              "[" + errorContent.status + "] " + JSON.stringify(errorContent.msg)
            );
          });
          return;
        }
        handleClickVariant("warning", "["+errorContent.status+"] "+JSON.stringify(errorContent.msg));
      });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    // eslint-disable-next-line no-console
    console.log({
      email: data.get("email"),
      password: data.get("password"),
      name: data.get("username"),
    });
    // 입력 값 검사
    if (validationCheck(data)) regitsterRequest(data);
  };

  return (
    <ThemeProvider theme={theme}>
      <Container component="main" maxWidth="xs">
        {/* Kickstart an elegant, consistent, and simple baseline to build upon. */}
        <CssBaseline />
        <Box
          sx={{
            marginTop: 8,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: "secondary.main" }}>
            <LockOutlinedIcon />
          </Avatar>
          <Typography component="h1" variant="h5">
            Register
          </Typography>
          <Box
            component="form"
            onSubmit={handleSubmit}
            noValidate
            sx={{ mt: 1 }}
          >
            <TextField
              margin="normal"
              required
              helperText={errorMsgEmail}
              fullWidth
              id="email"
              label="Email Address"
              name="email"
              autoComplete="email"
              autoFocus
            />
            <TextField
              margin="normal"
              required
              helperText={errorMsgPw}
              fullWidth
              name="password"
              label="Password"
              //   type="password"
              id="password"
              autoComplete="current-password"
            />
            <TextField
              margin="normal"
              required
              helperText={errorMsgUserName}
              fullWidth
              name="username"
              label="username"
              id="username"
              autoComplete="name"
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              Register
            </Button>
            <Grid container>
              <Grid item xs>
                <Link href="/" variant="body2">
                  🏡Back to Sign in
                </Link>
              </Grid>
            </Grid>
          </Box>
        </Box>
      </Container>
    </ThemeProvider>
  );
}
export default function out() {
  return(
    <SnackbarProvider maxSnack={3}>
      <Register/>
    </SnackbarProvider>
  );
}