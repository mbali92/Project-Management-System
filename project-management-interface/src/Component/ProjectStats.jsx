import axios from 'axios';
import Sidebar from './Sidebar'
import Navbar from './Navbar';
import { Chart } from 'react-google-charts';
import React, { useState, useEffect } from 'react';



const ProjectStats = () => {

    const [stats, setProjectStats] = useState({number_of_projects: 0,number_of_tables: 0,number_of_tasks: 0
    });

  

    const [monthlyStats, setMonthlyStats] = useState([]);

    const monthNames = [
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", 
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    ];



    useEffect(() => {
        const fetchUserData = async () => {
            const loggedAdmin = JSON.parse(sessionStorage.getItem("systemUser"));
            if (loggedAdmin) {
                try {
                const response = await axios.get(`http://localhost:8080/admin/projectStats`, {
                    withCredentials:true
                });
                if(response.data) {
                    setProjectStats(response.data);
                   
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
        ["Projects", stats.number_of_projects],
        ["Tables", stats.number_of_tables],
        ["Task", stats.number_of_tasks]
    ];

    
    const options = {
        legend: {
            position: 'bottom', // Set legend position
            alignment: 'right', // Align legend items to the center
            margin: '10', // Set margin around legend
        },
    };

    useEffect(() => {
        const fetchMonthlyStats = async () => {
            const loggedAdmin = JSON.parse(sessionStorage.getItem("systemUser"));
            if (loggedAdmin) {
                try {
                    const response = await axios.get(`http://localhost:8080/admin/projectspermonth`, {
                        withCredentials:true
                    });

                    if (response.data) {
                        setMonthlyStats(response.data);
                        console.log(response.data);
                    }
                } catch (error) {
                    console.error('Error fetching monthly stats:', error);
                }
            }
        };
        fetchMonthlyStats();
    }, []);

    const options1 = {
        legend: {
            position: 'bottom',
            alignment: 'top',
            margin: '10'
        },
    };

   const data = [
        ['Month', 'Number of Projects'],
        ...monthlyStats.map(([month, projectCount]) => [monthNames[month - 1], projectCount])
    ];







  return (
    <>
        <div className="page-row">
            <Sidebar/>
            <div className="project-wrapper">
                <Navbar/>
                <div className="users_dashboard page-row">
                        <div className="user_piechart">
                                    <h6>Total number of projects,tables,tasks</h6>
                                    <Chart
                                        chartType="PieChart"
                                        data={Datastats}
                                        options={options}
                                        width={"100%"}
                                        height={"400px"}
                                        />
                         </div>
                </div>

                <div className="users_dashboard page-row">
                <div className="user_bargraph">
                <h6>Number of Projects per Month</h6>
                            <Chart
                                chartType="LineChart"
                                data={data}
                                options={options1}
                                width={'129%'}
                                height={'400px'}
                            />
                         </div>

                </div>


           
            </div>

        </div>

    </>
  )
}

export default ProjectStats
