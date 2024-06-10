import React, { useState, useEffect } from "react";
import loginImageOne from "../assets/login-image2.png";
import loginImageTwo from "../assets/login-image1.png";
import axios from "axios";
import { useForm } from "react-hook-form";
import { jwtDecode } from "jwt-decode";
import Cookies from "js-cookie";
import { loginUserDetails } from "./LoginUserDetails";

function Login() {

    const [showRegister, setShowRegister] = useState("none");
    const [emailMessage, setemailMessage] = useState("none");
    const [passwordMessage, setpasswordMessage] = useState("none");
    const [badCredentials, setBadCredentials] = useState("");


  const { register, handleSubmit, setValue } = useForm({
    shouldUseNativeValidation: true,
    defaultValues: {
      fullName: "",
      email: "",
      password: "",
      re_password: "",
    },
  });


    const login = async (userDetails) => {
        if (showRegister == "none") {
            delete userDetails.fullName;
            delete userDetails.re_password;
            try {
                const response = await axios.post("http://localhost:8080/auth/loginUser",userDetails);
                if (response.data.message == "Login Successful") {
                    setBadCredentials("none");
                    loginUserDetails(response);
                }
                else {
                    setBadCredentials(response.data.message)
                    console.log(response.data.status)
                }
                } catch (error) {
                console.error("Error logging in: ", error);
            }
    } else {
      if (userDetails.password === userDetails.re_password) {
        setpasswordMessage("none");
        delete userDetails.re_password;
        try {
          const response = await axios.post(
            `http://localhost:8080/auth/registerUser`,
            userDetails
          );
          if (response.data) {
            setShowRegister("none");
            setemailMessage("block");
          }
        } catch (error) {
          console.error("Error adding task: ", error);
        }
      } else {
        setpasswordMessage("block");
      }
    }
  };

    const passwordChange = (e) => {
        e.preventDefault();
        sessionStorage.setItem("sentEmail", "requestedEmail");
        window.location.href = "/privacy";
    }

    return (
      <>
        <div className="login_page">
              <div className="page-row">
                  <div className="user_login">
                      <div className="user_login_details">
                            <div>
                                <h5 style={{display:showRegister}}>Please Register before login</h5>
                                <p style={{ display: showRegister }}>New to our system? please register your
                                details to use the system</p>
                            </div>
                            <div style={{display:showRegister == "block"?"none":"block"}}>
                                <h5 >Please Login</h5>
                                <p>Thank you for choosing us, if registered please login</p>
                            </div>
                            {
                                showRegister == "none" ? 
                                    <span className='logintoogleText' onClick={() => setShowRegister("block")}>Register on this page</span> :
                                    <span className='logintoogleText' onClick={() => setShowRegister("none")}> Login on this page</span>
                            }
                            <form action="" onSubmit={handleSubmit(login)} >
                              <div style={{display:showRegister}}>
                                <h6>Full name</h6>
                                <input  {...register("fullName",{required: showRegister == "block" ? true:false})} id='fullname' name ='fullName' type="text" placeholder='fullname' />
                              </div>
                              <h6>Email</h6>
                                <input {...register("email",{required:"please enter email"})} type="text" id='email' name="email"  placeholder='email' />
                               <h6>Password</h6>
                                <input  {...register("password", { required: "please enter password" })} id='password' type="text" name="password" placeholder='password' />
                                <p style={{ display: showRegister == "none" && badCredentials ? "block" : "none" }}>{badCredentials}</p>
                                <a href='' onClick={e =>passwordChange(e)} style={{display:showRegister == "none" ?"block" :"none"}}  id='forgot_password'>Forgot Password ?</a>
                               <div style={{display:showRegister}}>
                                    <h6>Re-enter password</h6>
                                    <p style={{display:passwordMessage}} id="wrong_password">Attention: Passwords do not match, please ensure they match to proceed</p>
                                    <input {...register("re_password", { required: showRegister == "block" ? true : false })} name='re_password' id='password' type="text" placeholder='password' />
                                </div>
                                {
                                    showRegister == "block" ? <button type='submit'>Register</button> :
                                    <button  type='submit'>Login</button>
                                }
                            </form>
                            <h6 style={{display:emailMessage}} id='confirm_message'>Thank you for registering please confirm via email before logging in </h6>
                            <div className="third_party_login">
                                <p id=''>Login with other sites</p>
                                <div className='third_party_links'>
                                    <a href="http://localhost:8080/oauth2/authorization/github">
                                        <i className="lni lni-github-original"></i>
                                        <span>Github</span>
                                    </a>
                                    <a href="http://localhost:8080/oauth2/authorization/google">
                                        <i className="lni lni-google"></i>
                                        <span>Google</span>
                                    </a>
                              </div>
                            </div>
                      </div>
                  </div>
                  <div className="login_project_description">
                      <div className="login_project_title">
                          <h1>Hello Welcome, to Project Name</h1>
                          <p>Hello and welcome to ProjectMaster! We're delighted to have you join
               our community of project managers and teams who are achieving remarkable success with our powerful project
                management platform.
              </p>
            </div>
            <div className="login_page_images">
              <img src={loginImageTwo} id="login_image_one" alt="" />
              <img src={loginImageOne} id="login_image_two" alt="" />
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default Login;
