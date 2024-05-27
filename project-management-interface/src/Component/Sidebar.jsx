
import React,{useEffect,useState} from 'react'
import { useNavigate } from 'react-router-dom';
import logo from "../assets/logo.png";
import axios from 'axios';
import Cookies from 'js-cookie';

function Sidebar() {
  const [project, setProject] = useState([]);
  const [activeLink, setactiveLink] = useState("");
  const [showlinks, setshowlinks] = useState("none");
  const [userRole, setuserRole] = useState();
  
  useEffect(() => { 
    const systemUser = JSON.parse(sessionStorage.getItem("systemUser"));
    if (systemUser) {
      const foundRole = systemUser.role.filter(user => user == "USER" || user == "ADMIN")
      setuserRole(foundRole[0])
    }
    setactiveLink(window.location.href)

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
            "id":element.project_id
            })
          });
          sessionStorage.setItem("userProjects", JSON.stringify(projectDetails))
        }
        } catch (error) {
          console.error('Error fetching data:', error);
      }
    };
    fetchData();
  },[]);
  const loadProject =(project_id)=>{
    sessionStorage.setItem("projectId",project_id);
    window.location.href = "project";
  }
  const navigate = useNavigate();

  const handleClick = () => {
    navigate('/feedbackAdmin');
  };

  const displayLinks = (e) => {
    e.preventDefault();
    setactiveLink("projects")
    showlinks == "block" ? setshowlinks("none") : setshowlinks("block");
  }
  const moveActive = (linkType, e) => {
    e.preventDefault();
    window.location.href = "/" + linkType;
    setshowlinks("none");
  } 

  return (
    <>
    <div className="sidebar">
        <h6 className='logo'><img src={logo} alt="" /><span> ProjectGuru</span></h6>
        <a href='/createproject' id='sidebar-create-btn'>Create project <i className="lni lni-plus"></i></a>
        <div className="sidebar-links">
          <div  style={{ display: userRole == "USER" ? "block" : "none" }}>
              <span className='sidebar-subtitle'>dashboard</span>
              <a href="" onClick={(e)=>moveActive("home",e)} className={activeLink.includes("home") ? "activelink" :""}><i className="lni lni-home"></i> Home </a>
              <a href="" className={activeLink == "projects" ? "activelink" :""} onClick={(e)=>displayLinks(e)}>
                <i className="lni lni-briefcase"></i>
                User Projects
                <i className="lni lni-chevron-down"></i>
              </a>
                {
                  project ? project.map((item,project_index)=>
                    <p style={{display:showlinks}} className='sub-links' key={project_index} onClick={()=>loadProject(item.project_id)}> {item.title} </p>
                  ):""
              }
              <a href="/" className={activeLink.includes("help") ? "activelink" :""} onClick={(e)=>moveActive("help",e)}><i id='info' className="lni lni-information"></i> Guide </a>
          </div>
          <div className='admin_dash' style={{display: userRole == "ADMIN" ? "block":"none"}}>
            <span className='sidebar-subtitle'>Adminstartion</span>
            <a href="users"><i className="lni lni-users"></i>Users</a>
            <a href=""><i className="lni lni-briefcase-alt"></i>Project stats</a>
            <a href=""><i className="lni lni-cogs"></i>System usage</a>
            <a href="" onClick={handleClick}><i className="lni lni-check-box"></i>Feedback</a>
          </div>
        </div>
    </div>
    </>
  )
}

export default Sidebar