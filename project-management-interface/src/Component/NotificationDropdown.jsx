import './styles/NotificationDropDown.css';
import imageOne from "../assets/topnav-image1.jpg";
import { useState, useEffect } from "react";
import axios from 'axios';

function NotificationDropDown({ userNot }) {
    const [showTrue, setShowTrue] = useState(true);

	// Marking the text as green if opened and red if not opned
    // const handleOpenMessage = (id) => {
    //     setData(prevData => {
    //         return prevData.map(item => {
    //             if (item.Id === id) {
    //                 return { ...item, opened: !item.opened };
    //             } else {
    //                 return item;
    //             }
    //         });
    //     });
    // };

    const handleCheckedBoxChange = () =>{
        setShowTrue(!showTrue)
    }

    const filteredItems = userNot.filter(item =>
        item
        // item.opened === showTrue
    );
    
    return(
        <div className="NotificationDropDowncontainer">
            <div className="msg_header">
                <div className="active">
                    <h4>User Notifications</h4>
                </div>
                <div className="header-icons">
                    <label className="switch">
                        <input type="checkbox" checked={showTrue} onChange={handleCheckedBoxChange}/>
                        <span className="slider"></span>
                    </label>
                </div>
            </div>

            <div className="chat-page">
                <div className="msg-inbox">
                    <div className="chats">
                        <div className="msg-page">
                            {filteredItems.map(item => (
                                <div className="recieved-chats" key={item.Id} onClick={() => handleOpenMessage(item.Id)}>
                                    <div className="recieved-chats-img">
                                        <img src={imageOne} alt="" />
                                    </div>
                                    <div className="recieved-msg" >
                                        <div className="recieved-msg-inbox">
                                            <div className="paragrough_holder">
                                                <div className="content">
                                                <h5>
                                                    @{item.fullName}
                                                </h5>
                                                <p>
                                                    {item.message}
                                                </p>
                                                </div>
                                                {/* <div className="State" style={{ backgroundColor: item.opened ? 'rgb(154, 240, 154)' : '#339AF0', display:"none" }}>
                                                    <div className="State_color_Strip" style={{ color: item.opened ? 'rgb(154, 240, 154)' : '#339AF0' }} >.</div>
                                                </div> */}
                                            </div>
                                            <span className="time">{item.time}</span>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default NotificationDropDown;
