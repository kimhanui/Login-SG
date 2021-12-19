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
import React, { useState } from "react";
import { SnackbarProvider, useSnackbar } from "notistack";

const theme = createTheme();

// 클래스 안 function 정의 못하는 듯? (React.Component에서 제공하는 메서드 오버라이드하는거면 몰라도)
function SignIn() {
  const [errorMsgEmail, setErrorMsgEmail] = useState("");
  const [errorMsgPw, setErrorMsgPw] = useState("");
  const [verifiedEmail, setVerifiedEmail] = useState(
    window.localStorage.getItem("email")
  );
  const [verifiedRole, setVerifiedRole] = useState(
    window.localStorage.getItem("role")
  );
  const errorMsg = {
    EMAIL: "이메일 형식으로 적어주세요. (한글 x)",
    PW: "영어 알파벳, 숫자, 일부 특수기호만 입력해주세요. (4~12자리)",
  };
  let emailCheck = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/;
  let passwordCheck = /[0-9a-zA-Z!@#$%^&*]/;

  //////////// snackbar handling ////////////
  const { enqueueSnackbar } = useSnackbar();
  const handleClickVariant = (variant, msg) => {
    // variant could be success, error, warning, info, or default
    enqueueSnackbar(msg, { variant: variant });
  };
  ///////////////////////////////////////////

  const validationCheck = (data) => {
    const email = data.get("email");
    const password = data.get("password");
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

    return result;
  };

  const signInRequest = (data) => {
    axios
      .post(
        "http://localhost:8080/user/login",
        {
          email: data.get("email"),
          password: data.get("password"),
        },
        {
          responseType: "application/json",
        }
      )
      .then(function (response) {
        console.log("signIn resp: " + JSON.stringify(response.data));
        window.localStorage.setItem("email", response.data.email);
        window.localStorage.setItem("role", response.data.role);
        window.localStorage.setItem("accessToken", response.data.accessToken);
        window.localStorage.setItem("refreshToken", response.data.refreshToken);
        setVerifiedEmail(response.data.email);
        setVerifiedRole(response.data.role);
        handleClickVariant("success", "정상적으로 로그인되었습니다.");
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
        handleClickVariant(
          "warning",
          "[" + errorContent.status + "] " + JSON.stringify(errorContent.msg)
        );
      });
  };

  const signOutRequest = () => {
    const accessToken = window.localStorage.getItem("accessToken");
   
    if(accessToken===null){
      handleClickVariant(
        "warning","이미 로그인하지 않은 상태 입니다."
      );
    }else{
    axios
      .get("http://localhost:8080/user/logout", {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
        responseType: "application/json",
      })
      .then(function (response) {
        console.log("signIn resp: " + JSON.stringify(response.data));
        window.localStorage.removeItem("email");
        window.localStorage.removeItem("role");
        window.localStorage.removeItem("accessToken");
        window.localStorage.removeItem("refreshToken");
        setVerifiedEmail("");
        setVerifiedRole("");
        handleClickVariant("success", "정상적으로 로그아웃되었습니다.");
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
        handleClickVariant(
          "warning",
          "[" + errorContent.status + "] " + JSON.stringify(errorContent.msg)
        );
      });
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    // eslint-disable-next-line no-console
    console.log("form:", {
      email: data.get("email"),
      password: data.get("password"),
    });
    // 입력 값 검사
    if (validationCheck(data)) signInRequest(data);
  };

  return (
    <ThemeProvider theme={theme}>
      <Container component="main" maxWidth="xs">
        {/* Kickstart an elegant, consistent, and simple baseline to build upon. */}
        <CssBaseline />
        <Grid container>
          <Grid item xs>
            <Link href={window.localStorage.getItem("role")==="ADMIN"?"/admin":"/"} variant="body2">
              🔒Only Admin
            </Link>
          </Grid>
          <Grid item>
            <Button onClick={signOutRequest} variant="body2">
              🚪Logout
            </Button>
          </Grid>
        </Grid>
        <Grid container>
          <Grid item xs>
            User: {verifiedEmail}
            <br />
            Role: {verifiedRole}
          </Grid>
        </Grid>
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
            Sign in
          </Typography>
          <Box
            component="form"
            onSubmit={handleSubmit}
            noValidate
            sx={{ mt: 1 }}
          >
            <TextField
              required
              helperText={errorMsgEmail}
              margin="normal"
              fullWidth
              id="email"
              label="Email Address"
              name="email"
              autoComplete="email"
              autoFocus
            />
            <TextField
              required
              helperText={errorMsgPw}
              margin="normal"
              fullWidth
              name="password"
              label="Password"
              type="password"
              id="password"
              autoComplete="current-password"
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              Sign In
            </Button>
            <Grid container>
              <Grid item xs>
                <Link href={window.localStorage.getItem("role")===null ?"/findmypw":"/"} variant="body2">
                  Forgot password?
                </Link>
              </Grid>
              <Grid item>
                <Link href={window.localStorage.getItem("role")===null ?"/register":"/"} variant="body2">
                  {"Register an account!"}
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
  return (
    <SnackbarProvider maxSnack={3}>
      <SignIn />
    </SnackbarProvider>
  );
}
