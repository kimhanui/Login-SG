import React, { useState } from "react";
import axios from "axios";
import Button from "@mui/material/Button";
import { SnackbarProvider, useSnackbar } from "notistack";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Link from "@mui/material/Link";
import Grid from "@mui/material/Grid";

function Admin() {
  const dataset = [createData(1, "dummy@email", "dummykim", "member")];
  function createData(no, email, name, role) {
    return { no, email, name, role };
  }

  const addAllUserData = (data) => {
    for (let i = 0; i < data.length; i++) {
      dataset.push(
        createData(i + 1, data[i].email, data[i].name, data[i].role)
      );
    }
  };
  //////////// snackbar handling ////////////
  const { enqueueSnackbar } = useSnackbar();
  const handleClickVariant = (variant, msg) => {
    // variant could be success, error, warning, info, or default
    enqueueSnackbar(msg, { variant: variant });
  };
  ///////////////////////////////////////////

  const userListRequest = () => {
    const accessToken = window.localStorage.getItem("accessToken");
    if (accessToken == null) {
      handleClickVariant("error", "로그인을 먼저 해주세요");
    } else {
      axios
        .get("http://localhost:8080/admin/userlist", {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
          responseType: "application/json",
        })
        .then(function (response) {
          console.log("response length:" + JSON.stringify(response.data));
          addAllUserData(response.data);
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
                  refreshToken: `${window.localStorage.getItem(
                    "refreshToken"
                  )}`,
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
            "error",
            "[" + errorContent.status + "] " + JSON.stringify(errorContent.msg)
          );
        });
    }
  };

  const updateUserRequest = (row) => {
    const accessToken = window.localStorage.getItem("accessToken");
    axios
      .post("http://localhost:8080/admin/updateuser", {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
        responseType: "application/json",
      })
      .then(function (response) {
        console.log(JSON.stringify(response));
        // TODO: 사용자 정보 업데이트
      })
      .catch(function (error) {
        console.log("error.response", error.response);
        const errorContent = error.response.data;
        console.log("signIn error: " + JSON.stringify(errorContent.msg));
        handleClickVariant(
          "warning",
          "[" + errorContent.status + "] " + JSON.stringify(errorContent.msg)
        );
      });
  };

  userListRequest();
  console.log("after init", JSON.stringify(dataset));

  return (
    <div>
      <TableContainer component={Paper}>
        <Grid container>
          <Grid item xs>
            <Link href="/" variant="body2">
              🏡Return to Sign in Page
            </Link>
          </Grid>
        </Grid>
        <Table sx={{ minWidth: 650 }} aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell align="right">No.</TableCell>
              <TableCell align="right">Email</TableCell>
              <TableCell align="right">Name&nbsp;</TableCell>
              <TableCell align="right">Role&nbsp;</TableCell>
              <TableCell align="right">Update&nbsp;</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {dataset.map((row) => (
              <TableRow
                key={row.name}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
              >
                <TableCell align="right">{row.no}</TableCell>
                <TableCell align="right">{row.email}</TableCell>
                <TableCell align="right">{row.name}</TableCell>
                <TableCell align="right">{row.role}</TableCell>
                <TableCell align="right">
                  <Button onClick={(row) => updateUserRequest}>수정</Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}

export default function out() {
  return (
    <SnackbarProvider maxSnack={3}>
      <Admin />
    </SnackbarProvider>
  );
}

{
}
