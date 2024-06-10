import { Outlet, Navigate, useHref } from "react-router-dom";
import React,{useEffect,useState} from "react";
import axios from 'axios';
import { refreshToken } from "./RefreshToken";


const PrivateRoutes = () => {
  const [user, setUser] = useState(null);
  
  useEffect(() => {
    const fetchData = async () => {
      try {
        const userFound = await refreshToken();
        setUser(userFound)
      } catch (error) {
        console.error('Error fetching data:', error)
        setUser(false)
      }
    }
    fetchData();

    return () => {}
  }, []);
  
  if(!user) {
    return null;
  }
    
  return(
    user ? <Outlet/>:<Navigate to={"/login"} />
  );
}
export default PrivateRoutes;