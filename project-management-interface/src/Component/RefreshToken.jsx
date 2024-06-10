import axios from 'axios';
import { loginUserDetails } from './LoginUserDetails';
import Cookies from "js-cookie";

export const refreshToken = async () => {
   const credentialsToken = Cookies.get("refreshToken")
   if (credentialsToken) {
      try {
        const response = await axios.get(`http://localhost:8080/auth/refreshTokenLogin/${credentialsToken}`);
         if(response.data.responseMessage == "SUCCESS") {
            //call set cookies method  
            loginUserDetails(response)
            return true;
         }
        } catch (error) {
         console.error('Error fetching data:', error);
         window.location.href ="/login"
         console.log(error)
      }
   }else{ window.location.href ="/login"}
    
} 