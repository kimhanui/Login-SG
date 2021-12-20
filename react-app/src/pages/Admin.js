import React, { useState, useEffect } from "react";
import axios from "axios";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
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
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";

function CustomSelectbox(props) {
  const row = props.row;
  const [role, setRole] = useState(row.role);
  return (
    <div>
    <Select id="role" value={role} onChange={e => {setRole(e.target.value)}}>
      <MenuItem value="member">member</MenuItem>
      <MenuItem value="admin">admin</MenuItem>
    </Select>
    </div>
  );
}


function Admin() {
  const [dataset, setDataset] = useState([]);
  let userCnt = 0;
  //화면을 그리기 전에 한번만 실행한다.(<-> useState: 값이 바뀔 때마다 컴포넌드를 초기화한다..)
  useEffect(() => {
    userListRequest();
  }, []);

  //////////// snackbar handling ////////////
  const { enqueueSnackbar } = useSnackbar();
  const handleClickVariant = (variant, msg) => {
    // variant could be success, error, warning, info, or default
    enqueueSnackbar(msg, { variant: variant });
  };
  ///////////////////////////////////////////

  const userListRequest = async () => {
    const accessToken = window.localStorage.getItem("accessToken");
    if (accessToken == null) {
      handleClickVariant("error", "로그인을 먼저 해주세요");
    } else {
      try {
        const resp = await axios.get("http://localhost:8080/admin/userlist", {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
          responseType: "application/json",
        });
        setDataset(resp.data);
      } catch (error) {
        console.log("error:", error);
        const errorContent = error.response.data;
        if (!error.response.hasOwnProperty("data")) {
          console.log("signIn error: " + JSON.stringify(error.response));
        } else if (
          error.response.hasOwnProperty("data") &&
          error.response.data.status == "REQUEST_TIMEOUT"
        ) {
          try {
            console.log("refresh token으로 access 재발급");
            const respToRefreshToken = await axios.post(
              "http://localhost:8080/user/reissueactoken",
              {
                email: `${window.localStorage.getItem("email")}`,
                refreshToken: `${window.localStorage.getItem("refreshToken")}`,
              },
              {
                responseType: "application/json",
              }
            );
            window.localStorage.setItem(
              "accessToken",
              respToRefreshToken.data.accessToken
            );
            handleClickVariant(
              "info",
              "인증을 연장했습니다. 다시 요청해주세요"
            );
          } catch (err) {
            handleClickVariant(
              "error",
              "[" +
                err.response.data.status +
                "] " +
                JSON.stringify(err.response.data.msg)
            );
          }
        } else {
          handleClickVariant(
            "error",
            "[" + errorContent.status + "] " + JSON.stringify(errorContent.msg)
          );
        }
      }
    }
  };

  // FIXME: requestData에 정확한 `data`를 넣거나, 
  // 전체 data를 관리하는 const를 두고 변경 사항이 생길 때마다 const에 반영해서  map의 `index`로 접근하기 
  const updateUserRequest = (requestData) => {
    const accessToken = window.localStorage.getItem("accessToken");
    console.log(JSON.stringify(requestData));
    try {
       axios.post("http://localhost:8080/admin/updateuser", requestData, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
        responseType: "application/json",
      });
      handleClickVariant(
        "success", "해당 사용자의 정보를 수정했습니다.");
    } catch (error) {
      console.log("error.response", error.response);
      const errorContent = error.response.data;
      console.log("signIn error: " + JSON.stringify(errorContent.msg));
      handleClickVariant(
        "warning",
        "[" + errorContent.status + "] " + JSON.stringify(errorContent.msg)
      );
    }
  };

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
              <TableCell align="right">Password&nbsp;</TableCell>
              <TableCell align="right">Update&nbsp;</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {dataset.map((row, index) => (
              <TableRow
                key={row.name}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
              >
                <TableCell id={row.email} name="no" align="right">
                  {++userCnt}
                </TableCell>
                <TableCell id={row.email} name="email" align="right">
                  <TextField
                    margin="normal"
                    fullWidth
                    id="email"
                    value={row.email}
                    autoFocus
                  />
                </TableCell>
                <TableCell id={row.email} name="name" align="right">
                  <TextField
                    margin="normal"
                    fullWidth
                    id="email"
                    value={row.name}
                    autoFocus
                  />
                </TableCell>
                <TableCell id={row.email} name="role" align="right">
                  <CustomSelectbox row = {row}/>
                </TableCell>
                <TableCell id={row.email} name="password" align="right">
                  <TextField
                    margin="normal"
                    fullWidth
                    id="password"
                    value=""
                  />
                </TableCell>
                <TableCell id={row.email} align="right">
                  <Button 
                  // onClick={updateUserRequest({
                  //   email: `${row.email}`,
                  //   role: `${row.role}`
                  // })}
                  >
                    수정
                  </Button>
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
