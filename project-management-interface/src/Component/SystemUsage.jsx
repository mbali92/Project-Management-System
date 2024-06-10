import axios from 'axios';
import Sidebar from './Sidebar'
import Navbar from './Navbar';
import { Chart } from 'react-google-charts';
import React, { useState, useEffect } from 'react';



const SystemUsage = () => {

    const [stats, setSystemUsage] = useState({create_project_count: 0,
        create_table_count:0,create_task_count:0,
        create_taskDuplicate_count:0,create_taskDelete_count:0
    });



    useEffect(() => {
        const fetchUserData = async () => {
            const loggedAdmin = JSON.parse(sessionStorage.getItem("systemUser"));
            if (loggedAdmin) {
                try {
                const response = await axios.get(`http://localhost:8080/admin/projectCount`, {
                    withCredentials:true
                });
                if(response.data) {
                    setSystemUsage(response.data);
                    console.log(response.data)
                   
                }
                } catch (error) {
                    console.error('Error fetching data:', error);
                }
            }
        }
        fetchUserData();
    
        return() => {
            
        }
    }, []);


    const Datastats = [
        ["Type of User", "Total numbers"],
        ["Projects Usage Count", stats.create_project_count],
        ["Tables Usage Count", stats.create_table_count],
        ["Tasks Usage Count", stats.create_task_count],
        ["Duplicate Task Usage Count", stats.create_taskDuplicate_count],
        ["Delete Task Usage Count", stats.create_taskDelete_count]

        


        
    ];

    
    const options = {
        legend: {
            position: 'bottom', // Set legend position
            alignment: 'right', // Align legend items to the center
            margin: '10', // Set margin around legend
        },
    };

    
  return (
    <>
        <div className="page-row">
            <Sidebar/>
            <div className="project-wrapper">
                <Navbar/>
                <div className="users_dashboard page-row">
                        <div className="user_piechart">
                                    {/* <h6>System Usage Count</h6> */}
                                    <Chart
                                        chartType="PieChart"
                                        data={Datastats}
                                        options={options}
                                        width={"1200px"}
                                        height={"400px"}
                                        />
                         </div>
                </div>
            </div>

        </div>

    </>
  )
}

export default SystemUsage
