import React,{useEffect,useState} from 'react'
import { Outlet, Navigate, useHref } from "react-router-dom";
import { refreshToken } from "./RefreshToken";

function LoginRoute() {
  const fetchData = () => {
    window.location.href = "/home"
    // const userFound = true;
    // if (userFound) {
    //   window.location.href = "/home"
    // }
    //   try {
    //     const userFound = await refreshToken();
    //       if (userFound) {
    //         window.location.href = "/home"
    //     }
    //   } catch (error) {
    //       console.error('Error fetching data:', error)
    //       window.location.href = "/login"
    //   }
    // }
  }
    fetchData();
}

export default LoginRoute