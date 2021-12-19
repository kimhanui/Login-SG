import * as React from "react";
import axios from "axios";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import Link from "@mui/material/Link";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import { SnackbarProvider, useSnackbar } from "notistack";

const theme = createTheme();

function FindMyPw() {
  //////////// snackbar handling ////////////
  const { enqueueSnackbar } = useSnackbar();
  const handleClickVariant = (variant, msg) => {
    // variant could be success, error, warning, info, or default
    enqueueSnackbar(msg, { variant: variant });
  };
  ///////////////////////////////////////////

  const mailpwRequest = async (email) => {
    const accessToken = window.localStorage.getItem("accessToken");

    await axios
      .get("http://localhost:8080/user/mailpw?email=" + email, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
        responseType: "application/json",
      })
      .then(function (response) {
        console.log(JSON.stringify(response));
      })
      .catch(function (error) {
        const errorContent = error.response.data;
        console.log("signIn error: " + JSON.stringify(error.response));
        if (errorContent.status == "REQUEST_TIMEOUT") {
          console.log("refresh token으로 access 재발급");
          axios
            .post(
              "http://localhost:8080/user/reissueactoken",
              {
                email: `${window.localStorage.getItem("email")}`,
                refreshToken: `${window.localStorage.getItem("refreshToken")}`,
              },
              {
                responseType: "application/json",
              }
            )
            .then(function (response) {
              console.log(JSON.stringify(response.data));
              window.localStorage.setItem(
                "accessToken",
                response.data.accessToken
              );
              handleClickVariant(
                "info",
                "인증을 연장했습니다. 다시 요청해주세요"
              );
            })
            .catch(function (error) {
              handleClickVariant(
                "error",
                "[" +
                  errorContent.status +
                  "] " +
                  JSON.stringify(errorContent.msg)
              );
            });
          return;
        }
        handleClickVariant(
          "warning",
          "[" + errorContent.status + "] " + JSON.stringify(errorContent.msg)
        );
      });
      handleClickVariant(
        "info","새로운 비밀번호를 메일로 전송했습니다.");
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    mailpwRequest(data.get("email"));
  };

  return (
    <ThemeProvider theme={theme}>
      <Container component="main" maxWidth="xs">
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
            Find My Password
          </Typography>
          <Box
            component="form"
            onSubmit={handleSubmit}
            noValidate
            sx={{ mt: 1 }}
          >
            <TextField
              required
              margin="normal"
              fullWidth
              id="email"
              label="Email Address"
              name="email"
              autoComplete="email"
              autoFocus
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              Receive email
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
  return (
    <SnackbarProvider maxSnack={3}>
      <FindMyPw />
    </SnackbarProvider>
  );
}
