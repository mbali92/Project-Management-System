import Cookies from "js-cookie";

export const loginUserDetails = (response) => {
    const userDetails = { email: response.data.email, fullName: response.data.fullname, role: response.data.role }
    sessionStorage.setItem("systemUser", JSON.stringify(userDetails))
    Cookies.set('jwtToken', response.data.jwtToken, {
        path: '/', expires: 365 
    });
    Cookies.set('refreshToken', response.data.refreshToken,{
        path: '/',expires: 365 
    });
    if ("responseMessage" in response.data) {
        console.log(response.data.responseMessage)
    }else{ window.location.href = "/";}
}