import React, { useEffect } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import { loginUserDetails } from "./LoginUserDetails";

const OAuth2Login = () => {
  const handleOAuth2LoginResponse = async () => {
    try {
      // Make a GET request to the backend to complete the login process
      const response = await axios.get(
        "http://localhost:8080/oauth/socialLogin",
        { withCredentials: true }
      );
      if (response.data.status == "OK") {
        loginUserDetails(response);
      } else {
        console.log("Error during login:", response.data.message);
        // Handle error scenario
        window.location.href = "/login";
      }
    } catch (error) {
      console.error("Error handling OAuth2 login response:", error);
      window.location.href = "/login";
    }
  };

  // Call the function to handle the OAuth2 login response
  console.log("Calling the handleOAuth2LoginResponse function");
  handleOAuth2LoginResponse();

  return <div>Loading...</div>; // TODO: add a loading indicator here
};

export default OAuth2Login;
