
import React from 'react';
import { Route, Routes } from 'react-router-dom';
import {SignIn, Register, FindMyPw, Admin} from './pages/index.js';

export default function App(){
  return(
    <Routes>
      <Route exact path="/" element={<SignIn/>}/>
      <Route path ="/register" element={<Register/>}/>
      <Route path="/findmypw" element={<FindMyPw/>}/>
      <Route path="/admin" element={<Admin/>}/>
    </Routes>
  )
}