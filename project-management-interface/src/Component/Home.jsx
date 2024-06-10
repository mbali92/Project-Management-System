import React, { useEffect,useState } from 'react'
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import imageOne from "../assets/hero-img2.png"

function Home() {
    const [user, setuser] = useState();
    const [userProjects, setuserProjects] = useState([]);

    useEffect(() => {
        const systemUser = JSON.parse(sessionStorage.getItem("systemUser"));
        const projects = JSON.parse(sessionStorage.getItem("userProjects"));
        if (systemUser) {
            setuser(systemUser)
        }
        if (projects) {
            setuserProjects(projects)
        }

        return () => {
        }
        
    }, []);
    const loadProject =(project_id)=>{
    sessionStorage.setItem("projectId",project_id);
    window.location.href = "project";
    }

  return (
      <>
          <div className="page-row">
              <Sidebar/>
            <div className="project-wrapper">
                <Navbar />
                <div className="home-section">
                      <div className="container">
                          <div className="home-header">
                               <div className="home-image">
                                  <img src={imageOne} alt="" />
                              </div>
                              <div className="home-title">
                                  <h2>Hello, {user ? user.fullName : ""}</h2>
                                  <div className="home_user_info_box">
                                      <div className='home_user_info'>
                                      <i className="lni lni-envelope"></i>
                                      <span>email@email.com</span>
                                  </div>
                                  <div className='home_user_info'>
                                      <i className="lni lni-calendar"></i>
                                      <span>joined: April 2, 2024</span>
                                  </div>
                                  </div>
                                  <p>Welcome to our Project Management System! We are excited to provide you with a robust and user-friendly platform to streamline your project planning, tracking, and collaboration.</p>
                                  
                              </div>
                          </div>
                          <div className="home-projects">
                              <h5> Your Projects </h5>
                              <div className="home-project-card-container">
                                  {
                                      userProjects ? userProjects.map(project =>
                                          <div key={project.id} onClick={() => loadProject(project.id)} className="home-project-cards">
                                              <div className="home_project_card_title">
                                                  <i className="lni lni-briefcase-alt"></i>
                                                   <h6> {project.title}</h6>
                                                    <span>Date created: 11 May,2024</span>
                                                    <p>Lorem ipsum, dolor sit amet consectetur adipisicing elit.
                                                        Facilis assumenda laboriosam adipisci tenetur.</p>
                                              </div>
                                              <div className="home_project_members">
                                                  <h6>Project members</h6>
                                                  <span className='task_assign_letter'>M</span>
                                                  <span className='task_assign_letter'>M</span>
                                              </div>
                                        </div>
                                      ):""
                                  }
                                  
                              </div>
                              <a href='' id='home_btn'><i className="lni lni-circle-plus"></i> Add projects</a>
                          </div>
                      </div>  
                </div>
            </div>
        </div>
      </>
  )
}

export default Home