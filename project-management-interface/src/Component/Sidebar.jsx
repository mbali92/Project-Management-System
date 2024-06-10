
import React,{useEffect,useState} from 'react'
import { useNavigate } from 'react-router-dom';
import logo from "../assets/logo.png";
import axios from 'axios';
import Cookies from 'js-cookie';
import { refreshToken } from './RefreshToken';

function Sidebar() {
  const [project, setProject] = useState([]);
  const [activeLink, setactiveLink] = useState("");
  const [projectLink, setprojectLink] = useState(0);
  const [showlinks, setshowlinks] = useState("none");
  const [userRole, setuserRole] = useState();
  const sixHoursInMilliseconds = 6 * 60 * 60 * 1000; 
  
  const showActiveProjectPage = () => {
    if (window.location.href.includes("project")) {  
      setprojectLink(sessionStorage.getItem("projectId"))
      setshowlinks("block")
      } 
     setactiveLink(window.location.href)
  }

  useEffect(() => { 
     showActiveProjectPage();
      const fiveHoursInMilliseconds = 5 * 60 * 60 * 1000; // 21,600,000 milliseconds

    const refreshTokenInterval = async () => {
      try {
        await refreshToken();
        console.log("Token refreshed successfully");
      } catch (error) {
        console.error("Failed to refresh token:", error);
      }
    };

    const setCookiesInterval = setInterval(refreshTokenInterval,fiveHoursInMilliseconds)

    const systemUser = JSON.parse(sessionStorage.getItem("systemUser"));
    if (systemUser) {
      const foundRole = systemUser.role.filter(user => user == "USER" || user == "ADMIN")
      setuserRole(foundRole[0])
    }
   
    console.log(window.location.href)
    const fetchData = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/user/fetchUserProject`, {
          withCredentials:true
        });
        if(response.data) {
          setProject(response.data)
          const projectDetails = []
          response.data.forEach(element => {
          projectDetails.push( {
            "title": element.title,
            "description": element.description,
            "owner": element.user.email,
            "id":element.projectId
            })
          });
          sessionStorage.setItem("userProjects", JSON.stringify(projectDetails))
          sessionStorage.setItem("projectStats", JSON.stringify(response.data))
        }
        } catch (error) {
          console.error('Error fetching data:', error);
      }
    };
    fetchData();
    return ()=> clearInterval(setCookiesInterval)
  },[]);
  const loadProject =(projectId)=>{
    sessionStorage.setItem("projectId", projectId);
    window.location.href = "project";
  }
  const navigate = useNavigate();
  const redirectback = () =>{
    navigate('/');
  } 
  const handleClick = () => {
    navigate('/feedbackAdmin');
  };

  const displayLinks = (e) => {
    e.preventDefault();
    setactiveLink("project_page");
    setprojectLink(0);
    showlinks == "block" ? setshowlinks("none") : setshowlinks("block");
  }
  const moveActive = (linkType, e) => {
    e.preventDefault();
    window.location.href = linkType;
    setshowlinks("none");
  } 

  return (
    <>
    <div className="sidebar">
        <h6 className='logo' onClick={redirectback}><img src={logo} alt="" /><span> ProjectGuru</span></h6>
        <a href='/createproject' id='sidebar-create-btn'>Create project <i className="lni lni-plus"></i></a>
        <div className="sidebar-links">
          <div  style={{ display: userRole == "USER" ? "block" : "none" }}>
              <span className='sidebar-subtitle'>dashboard</span>
              <a href="" onClick={(e)=>moveActive("/",e)} className={activeLink == "http://localhost:5173/" ? "activelink" :""}><i className="lni lni-home"></i> Home </a>
              <a href="" className={activeLink == "project_page" ? "activelink" :""} onClick={(e)=>displayLinks(e)}>
                <i className="lni lni-briefcase"></i>
                User Projects
                <i className="lni lni-chevron-down"></i>
            </a>
            <div className="sub_links">
              {
                  project ? project.map((item,project_index)=>
                    <p className={activeLink.includes("/project") && projectLink == item.projectId ? "activelink" :""} style={{display:showlinks}}  key={project_index} onClick={()=>loadProject(item.projectId)}><span></span> {item.title} </p>
                  ):""
                }
            </div> 
            <a href="/" className={activeLink.includes("/help") ? "activelink" :""} onClick={(e)=>moveActive("help",e)}><i id='info' className="lni lni-information"></i> Guide </a>
          </div>
          <div className='admin_dash' style={{display: userRole == "ADMIN" ? "block":"none"}}>
            <span className='sidebar-subtitle'>Adminstartion</span>
            <a href="users"><i className="lni lni-users"></i>Users</a>
            <a href="projectStats"><i className="lni lni-briefcase-alt"></i>Project stats</a>
            <a href="systemUsage"><i className="lni lni-cogs"></i>System usage</a>
            <a href="" onClick={handleClick}><i className="lni lni-check-box"></i>Feedback</a>
          </div>
        </div>
    </div>
    </>
  )
}

export default Sidebar