
import React,{useEffect} from 'react'
import axios from 'axios';

function Assign(props) {
  const assignTask = async (memebrId) => {
     try {
        const response = await axios.get(`http://localhost:8080/assignee/saveAssignee/${memebrId}/
        ${props.assignModel.assigneeId}`, {
           withCredentials:true
          });
          if(response.data) {
            // window.location.reload();
            console.log(response.data)
          }
          } catch (error) {
            console.error('Error fetching data:', error);
      }
  }
  
  return (
    <>
      <div className="assign_section" style={{height:props.assignModel.assigneeHieght +"%"}}>
        <div className="assign_container">
          <i className="lni lni-close" onClick={()=>props.closeMode("assigneeHieght",0,0,'')}></i>
           <div className="task_members">
            <h6>Task members</h6>
            {
              props.taskMembers ?
                props.taskMembers.map((taskTeam,i) =>
              <div key={i} className="task_member_name">
                  <div  className='assignee_details'>
                    <span className='assignee_letters'>M</span>
                    <div>
                      <span id='assignee_username'>{taskTeam.username}</span>
                      <span id='assignee_email'>email@email.com</span>
                    </div>
                  </div>
                  <i className="lni lni-circle-minus remove_from_task"></i>  
              </div>
            ):""}
          </div>
          <div className="project_members_list">
            <h6>Project members list: </h6>
            {props.projectPeople ? props.projectPeople.map((member,i ) =>
               <div key={i} className="project_members_names" onClick={()=>assignTask(member.member_id)}>
                  <div className="task_project_names_details">
                      <i className="lni lni-user" ></i>
                      <p>
                       <span>{member.username }</span>
                        <span>email@email.com</span>
                      </p>
                  </div>
                  <i className="lni lni-circle-plus invite_to_task"></i>
                </div>
            ):""}
          </div>
        </div>
      </div>
    </>
  )
}

export default Assign