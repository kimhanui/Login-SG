// import * as React from "react";
// import Alert from "@mui/material/Alert";
// import Stack from "@mui/material/Stack";
// import { AlertTitle } from "@mui/material";

// export default function BasicAlerts(props) {
//   const errorAlert = () => (
//     <Alert severity="error" onClose={() => {}}>
//       <AlertTitle>Error</AlertTitle>
//       <strong>{props.msg}</strong>
//     </Alert>
//   );

//   const warningAlert = () => (
//     <Alert severity="warning" onClose={() => {}}>
//       <AlertTitle>Warning</AlertTitle>
//       <strong>{props.msg}</strong>
//     </Alert>
//   );

//   const infoAlert = () => (
//     <Alert severity="info" onClose={() => {}}>
//       <AlertTitle>Info</AlertTitle>
//       <strong>{props.msg}</strong>
//     </Alert>
//   );

//   const successAlert = () => (
//     <Alert severity="success" onClose={() => {}}>
//         <AlertTitle>Success</AlertTitle>
//         <strong>{props.msg}</strong>
//     </Alert>
//   );

//   const selectAlert = () => {
//     if(props.select === "error") return errorAlert();
//     else if(props.select === "warning") return warningAlert();
//     else if(props.select === "info") return infoAlert();
//     else if(props.select === "success") return successAlert();
//   }
  
//   return (
//     <Stack sx={{ width: "100%" }} spacing={2}>
//       {selectAlert()}
//     </Stack>
//   );
// }
