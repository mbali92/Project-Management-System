import React, { useEffect, useState } from 'react';
import Sidebar from './Sidebar';
import Navbar from './Navbar';
import axios from 'axios';
import { Chart } from 'react-google-charts';

function Users() {
    const [userStats, setUserStats] = useState({
        admin_id: 0,
        number_of_owners: 0,
        number_of_members: 0,
    });

    const [pagination, setPagination] = useState([]);
    const pageSize = 4;
    const [index, setIndex] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const fetchUserData = async () => {
            const loggedAdmin = JSON.parse(sessionStorage.getItem("systemUser"));
            if (loggedAdmin) {
                try {
                    const response = await axios.get(`http://localhost:8080/admin/usersStats`, {
                        withCredentials:true
                    });
                    if (response.data) {
                        setUserStats(response.data);
                    }
                } catch (error) {
                    console.error('Error fetching data:', error);
                }
            }
        };
        fetchUserData();
    }, []);

    useEffect(() => {
        const fetchPaginatedData = async () => {
            const loggedAdmin = JSON.parse(sessionStorage.getItem("systemUser"));
            if (loggedAdmin) {
                try {
                const response = await axios.get(`http://localhost:8080/admin/paginate/${index}/${pageSize}`, {
                    withCredentials:true
                });
                if(response.data) {
                    setPagination(response.data.content);
                    setTotalPages(response.data.totalPages)
                    console.log(response.data.content)
                    console.log(response.data.totalPages)
                }
                } catch (error) {
                    console.error('Error fetching data:', error);
                }
            }
        }
        fetchPaginatedData();
    
        return() => {
            
        }
    }, [index,pageSize]);


    const Adminactive_User = async (user_id) => {
        try {
            const response = await axios.put(`http://localhost:8080/admin/activate/${user_id}`,{}, {
                withCredentials:true
            });
            if(response.data) {
                window.location.reload()
            }
            } catch (error) {
                console.error('Error fetching data:', error);
        }
    }

    const Admindeactive_User = async (user_id) => {
        try {
            const response = await axios.put(`http://localhost:8080/admin/deactivate/${user_id}`,{}, {
                withCredentials:true
            });
            if(response.data) {
                window.location.reload()
            }
            } catch (error) {
                console.error('Error fetching data:', error);
        }
    }

    


    const data = [
        ["Type of User", "Total numbers"],
        ["Owners", userStats.number_of_owners],
        ["Members", userStats.number_of_members],
    ];

    const options = {
        legend: {
            position: 'bottom',
            alignment: 'right',
            margin: '10',
        },
    };

    return (
        <div className="page-row">
            <Sidebar />
            <div className="project-wrapper">
                <Navbar />
                <div className="users_dashboard page-row">
                    <div className="admin-users">
                        <div className="">
                            <div className='admin_graph_titles'>
                                <h2>Users table</h2>
                                <p>List of all the users in the system</p>
                            </div>
                            <div className="users_table">
                                <div className="user_table_rows user_table_col_header">
                                    <div className='col_numbers user_tables_cols user_table_colNames'>
                                        <h6>User id</h6>
                                    </div>
                                    <div className='user_tables_cols user_table_colNames'>
                                        <h6>User details </h6>
                                    </div>
                                    <div className='user_tables_cols user_table_colNames'>
                                        <h6>Creation date</h6>
                                    </div>
                                    <div className='user_tables_cols user_table_colNames'>
                                        <h6>Last login</h6>
                                    </div>
                                    <div className='user_tables_cols user_table_colNames'>
                                        <h6>Account status</h6>
                                    </div>
                                    <div className='col_numbers user_tables_cols user_table_colNames'>
                                        <h6>Action</h6>
                                    </div>
                                </div>
                                {pagination.map ((member, index) => (
                                    <div className="user_table_rows user_table_row_details" key={index}>
                                        <div className="col_numbers user_id user_tables_cols">
                                            <p>{member.user_id}</p>
                                        </div>
                                        <div className="user_details user_tables_cols">
                                            <span className='user_letter'>{member.fullName.charAt(0)}</span>
                                            <p>
                                                <span>{member.fullName} </span>
                                                <span> {member.email}</span>
                                            </p>
                                        </div>
                                        <div className="date user_tables_cols">
                                            <p>
                                                <span>{member.created_at}</span>
                                            </p>
                                        </div>
                                        <div className="date user_tables_cols">
                                            <p>
                                                <span>12/02/25</span>
                                                <span>2:30pm</span>
                                            </p>
                                        </div>
                                        <div className="project_status user_tables_cols">
                                            <p id={member.enabled ? 'active' : 'deactive'}>{member.enabled ? "active" : "deactive"}</p>
                                            
                                        </div>
                                        <div className="actions col_numbers user_tables_cols">
                                            <i className="lni lni-pencil-alt" onClick={()=>Admindeactive_User(member.user_id)}></i>
                                            <i className="lni lni-circle-minus" onClick={()=>Adminactive_User(member.user_id)}></i>
                                        </div>
                                    </div>
                                ))} 
                            </div>
                            <div className="user_table_pages">
                                <div className="tables_total">
                                    <p>Showing {index * pageSize + 1} - {(index + 1) * pageSize} of {totalPages * pageSize} results</p>
                                </div>
                                <div className="user_page_slider">
                                    <button  onClick={() => setIndex(index + 1)} disabled={index === totalPages - 1}>Next</button>
                                    <p className='show'>{index + 1}</p>
                                    <button  onClick={() => setIndex(index - 1)} disabled={index === 0}>Prev</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="user_piechart">
                        <h6>Total number of users per roles</h6>
                        <Chart
                            chartType="PieChart"
                            data={data}
                            options={options}
                            width={"100%"}
                            height={"400px"}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Users;
