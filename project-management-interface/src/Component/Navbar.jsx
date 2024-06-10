import React, { useEffect, useState } from 'react';
import axios from 'axios'; 
import imageOne from "../assets/topnav-image1.jpg";
import Cookies from 'js-cookie';
import { Link } from 'react-router-dom';
import NotificationDropDown from './NotificationDropdown';
import { useNavigate } from 'react-router-dom';


function Navbar() {
  const [siteUser, setSiteUser] = useState();
  const [name, setName] = useState();
  const [logUser, setLogUser] = useState();
  const [userId, setUserId] = useState();
  const [userNotifications, setuserNotifications] = useState([]);

  const [dropDownBoxesHeight, setDropDownBoxesHeight] = useState({
    statusBox: 0, addColumn: 0, columnIndex: 0, editRow: 0, inviteBox: 0,
    filterBox: 0, sort: 0, rowId: 0, statusIndex: 0, table: 0, tableIndex: 0,
    commentBox: 0, commentTaskId: 0, assigneeHieght: 0, assigneeId: 0
  });

  useEffect(() => {
    setSiteUser(JSON.parse(sessionStorage.getItem("systemUser")));
    const foundUser = (JSON.parse(sessionStorage.getItem("systemUser")));
    setLogUser(foundUser)

    const getNotifications = async() => {
      try {
          const response = await axios.get(`http://localhost:8080/notification/getAll`, {
          withCredentials:true
          });
          if(response.data) {
          setuserNotifications(response.data)
          }
          } catch (error) {
          console.error('Error fetching data:', error);
        }
       }
       getNotifications();

    return () => {
    }
  }, []);

  const toogleDropDownBoxes = (contentType, height, rowIndex, indexType) => {
    setDropDownBoxesHeight(prevState => ({
      ...prevState,
      [contentType]: dropDownBoxesHeight[contentType] === 0 ? height : 0,
      [indexType !== "" ? indexType : '']: rowIndex
    }));
  }

  const handleDeactivateAccount = (e) => {
    e.preventDefault(); 
     setUserId(siteUser.user_id)

    // Send an AJAX request to deactivate the account
    axios.put(`/user/deactivate/${userId}`,{}, { 
      headers: {
        Authorization: `Bearer ${logUser.token}`, // Assuming token is stored in a variable
        'Content-Type': 'application/json'
    }
     })
      .then(response => {
        // Handle success response if needed
        console.log('Account deactivated successfully');
      })
      .catch(error => {
        // Handle error response if needed
        console.error('Error deactivating account:', error);
        console.log(userId)
      });
  };

  const logOut = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.delete("http://localhost:8080/user/logoutUser", {
        withCredentials:true
      })
      if (response.data.message == "user logged out") {
        Cookies.remove('jwtToken')
        Cookies.remove("refreshToken")
        window.location.href = "/"
      } else {
        console.log(response.data)
      }
    } catch(error) {
      console.error("user could'nt be logged out",error)
    }
  }
  const [openProfile, setOpenProfile] = useState(false);

  const navigate = useNavigate();
  const redirectback = () =>{
    navigate('/');
  } 
  
  return (
    <>
      <div className="navbar">
        <div className="page-row">
          <form action=""><input type="text" placeholder='Search here' />
            <button id='input_search_btn'><i className="lni lni-search"></i></button></form>
          <div className="nav-right-bar">
            <div className="nav-icons">
              <span id='not_length'>{ userNotifications.length}</span>
              <i className="lni lni-alarm" onClick={()=> setOpenProfile((prev)=> !prev)}></i>
                {
                  userNotifications.length != 0 ?  openProfile && <NotificationDropDown userNot={userNotifications} />:""
                }
            </div>
            <div className="profile" >
              <div className="profile_user_details" onClick={() => toogleDropDownBoxes("inviteBox", 380, 0, "")}>
                  <div className="task_assign_letter">{siteUser ? siteUser.fullName.charAt(0) : ""}</div>
                  <div className='top_profile_username'>
                    <span>{siteUser ? siteUser.fullName : ""}</span>
                    <span>{siteUser ? siteUser.email : ""}</span>
                  </div>
              </div>
              <div style={{ height: dropDownBoxesHeight.inviteBox }} className="profile_popup invite_members">
                <div className="member_invite_wrapper">
                  <h5 style={{ fontWeight: "bold" }}>ACCOUNT</h5>
                  <div style={{ position: "relative" }}>
                    <div className="profile_user_details">
                      <div className="task_assign_letter">{siteUser ? siteUser.fullName.charAt(0) : ""}</div>
                      <div className='top_profile_username'>
                        <span>{siteUser ? siteUser.fullName : ""}</span>
                        <span>{siteUser ? siteUser.email : ""}</span>
                      </div>
                    </div>
                  </div>
                  <a href='/switch' id="account_switch">Switch account</a>
                  <h5 style={{ fontWeight: "bold" }}>ProjectGuru</h5>
                  <div className="sidebar-links profile_links">
                    {/* <a href='/disable'>Deactivate account</a> */}
                    <a href='/settingspage'>Settings</a>
                    <a href='/help'>Help</a>
                    <a id="logout" onClick={(e)=>logOut(e)} href='/'>Logout</a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}

export default Navbar;
