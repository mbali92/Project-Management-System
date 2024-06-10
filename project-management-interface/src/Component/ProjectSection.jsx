import React, { useState, useEffect } from "react";
import Sidebar from "./Sidebar";
import Navbar from "./Navbar";
import axios from "axios";
import Comment from "./Comment";
import Assign from "./Assign";
import nextIcon from "../assets/icons8-next.png";
import previousIcon from "../assets/icons8-previous.png";
function ProjectSection() {
  const [editedTask, seteditedTask] = useState({});
  const [projectMembers, setProjectMembers] = useState([]);
  const [users, setUsers] = useState(["Alex Smith"]);
  const [memberFound, setmemberFound] = useState([{}]);
  const [loginMemberId, setloginMemberId] = useState();
  const [loggedInUser, setloggedInUser] = useState();
  const [taskComments, settaskComments] = useState([
    { comment_id: 0, username: "", date_created: "", commentBody: "" },
  ]);
  const [assignees, setassignees] = useState([]);

  const [searchTerm, setSearchTerm] = useState("");
  const [sortField, setSortField] = useState("tableName");
  const [direction, setSortDirection] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(3);
  const [totalTables, setTotalTables] = useState(0);
  const [currentItems, setCurrentItems] = useState([]);
  const [trackChange, setTrckChange] = useState(0);

  const [dropDownBoxesHeight, setDropDownBoxesHeight] = useState({
    statusBox: 0,
    addColumn: 0,
    columnIndex: 0,
    editRow: 0,
    inviteBox: 0,
    filterBox: 0,
    sort: 0,
    rowId: 0,
    statusIndex: 0,
    priorityIndex:0,
    priorityBox:0,
    table: 0,
    tableIndex: 0,
    commentBox: 0,
    commentTaskId: 0,
    assigneeHieght: 0,
    assigneeId: 0,
  });

  const [oneProject, setoneProject] = useState({
    projectId: 0,
    title: "",
    description: "",
    tableCount: 0,
    sortDirection: "",
    tables: [
      {
        tableId: 0,
        tableName: "New Table",
        projectId: 0,
        hiddenTaskColumns: {
          hiddenColumn_id: 1,
          priority: true
        },
        tasks: [
          {
            task_id: 0,
            title: "",
            description: "",
            owner: "",
            start_date: "",
            end_date: "",
            status: "",
            priority: "",
            projectName: "Project one",
            comments: [],
            assignees: [],
          },
        ],
      },
    ],
    memberList: [
      {
        member_id: 0,
        user_id: 0,
        projectId: 0,
        username: "",
      },
    ],
  });

  useEffect(() => {
    const projectId = sessionStorage.getItem("projectId");

    const fetchProject = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/user/getSingleProjectPaginationTables/${projectId}`,
          {
            params: {
              page: currentPage - 1,
              size: pageSize,
              sortField,
              search: searchTerm,
              sortDirection: direction,
            },
            withCredentials: true,
          }
        );
        if (response.data) {
          setoneProject(response.data);
          setCurrentItems(response.data.tables);
          setTotalTables(response.data.tableCount);
          setSortDirection(response.data.sortDirection);
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchProject();
  }, [searchTerm, sortField, direction, currentPage, pageSize, trackChange]);

  const updateProject = async (projectName, description, sort) => {
    try {
      const response = await axios.put(
        `http://localhost:8080/project/editProject`,
        {
          projectId: oneProject.projectId,
          title: projectName,
          description: description,
          sortDirection: sort,
        },
        { withCredentials: true }
      );
      setSortDirection(sort);
      console.log("Project updated successfully:", response.data);
    } catch (error) {
      console.error("Error updating project:", error);
    }
  };

  const toogleDropDownBoxes = (contentType, height, rowIndex, indexType) => {
    setDropDownBoxesHeight((prevState) => ({
      ...prevState,
      [contentType]: dropDownBoxesHeight[contentType] === 0 ? height : 0,
      [indexType !== "" ? indexType : ""]: rowIndex,
    }));
  };
  const createTable = async (projectNo) => {
    try {
      const response = await axios.post(
        `http://localhost:8080/table/createTable/${projectNo}`,
        {},
        {
          withCredentials: true,
        }
      );
      if (response.data) {
        setTrckChange(trackChange + 1);
        // window.location.reload();
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };
  const sendEditedRow = async (task, updatedValue, columnType) => {
    if (task[columnType] !== updatedValue) {
      if (columnType !== "status" || columnType !== "priority") {
        task[columnType] = updatedValue;
      }
      if (columnType == "status" ) {
        task.status = updatedValue;
      }
      if (columnType == "priority") {
        task.priority = updatedValue;
      }
      try {
        const response = await axios.put(`http://localhost:8080/task/editTask`,task,
          {
            withCredentials: true,
          }
        );
        if (response.data) {
          // window.location.reload();
          setTrckChange(trackChange + 1);
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    }
  };
  const Adding_Task = async (tableId) => {
    try {
      const response = await axios.post(
        `http://localhost:8080/task/createTask/${tableId}`,
        {},
        {
          withCredentials: true,
        }
      );
      if (response.data) {
        // window.location.reload();
        setTrckChange(trackChange + 1);
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };
  async function deleteTask(task_id, tableId) {
    try {
      const response = await axios.delete(
        `http://localhost:8080/task/deleteTaskById/${task_id}/${tableId}`,
        {
          withCredentials: true,
        }
      );
      if (response.data) {
        // window.location.reload()
        setTrckChange(trackChange + 1);
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  }
  const deleteTable = async (projectId, tableId) => {
    try {
      const response = await axios.delete(
        `http://localhost:8080/table/deleteTable/${projectId}/${tableId}`,
        {
          withCredentials: true,
        }
      );
      if (response.data) {
        // window.location.reload()
        setTrckChange(trackChange + 1);
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };
  const duplicateTask = async (tableId, task) => {
    try {
      const response = await axios.post(
        `http://localhost:8080/task/duplicateTask/${tableId}`,
        task,
        {
          withCredentials: true,
        }
      );
      if (response.data) {
        // window.location.reload()
        setTrckChange(trackChange + 1);
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };
  const updateTableName = async (newName, tableObg) => {
    if (tableObg.tableName != newName) {
      tableObg.tableName = newName;
      try {
        const response = await axios.put(
          `http://localhost:8080/table/updateTable`,
          tableObg,
          {
            withCredentials: true,
          }
        );
        if (response.data) {
          console.log(response.data);
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    }
  };

  const loadComments = (comments, taskid) => {
    settaskComments(comments);
    toogleDropDownBoxes("commentBox", 100, taskid, "commentTaskId");
  };
  const searchforMembers = async (username) => {
    if (username.length == 4 || (loggedInUser && username.length == 8)) {
      try {
        const response = await axios.post(
          `http://localhost:8080/member/searchMembers?fullnameLetters=${username}`,
          {},
          {
            withCredentials: true,
          }
        );
        if (response.data) {
          setmemberFound(response.data);
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    }
  };
  const inviteMember = async (userId) => {
    try {
      const response = await axios.get(
        `http://localhost:8080/member/inviteMember/${oneProject.projectId}/${userId}`,
        {
          withCredentials: true,
        }
      );
      if (response.data.projectId) {
        window.location.reload();
        setTrckChange(trackChange + 1);
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };
  const openAssignModel = (assignList, taskId) => {
    setassignees([]);
    if (assignList.length > 0) {
      setassignees(assignList);
    }
    toogleDropDownBoxes("assigneeHieght", 100, taskId, "assigneeId");
  };

  const goToPage = (page) => {
    setCurrentPage(page);
  };

  const toogleHiddenColumns = async (tableId, hiddenCols, colType, btnType) => {

    let changeColVisibility = false; 

    if (btnType == "hide" && hiddenCols[colType]) {
      hiddenCols[colType] = !hiddenCols[colType];
      changeColVisibility = true;
    }
    if (btnType == "show" && !hiddenCols[colType]) {
      hiddenCols[colType] = !hiddenCols[colType];
      changeColVisibility = true;
    }
    if (changeColVisibility) {
      try {
          const response = await axios.put(`http://localhost:8080/table/updateHiddenColumns/${tableId}`,
          hiddenCols,{ withCredentials: true }
          );
        if (response.data) {
           setTrckChange(trackChange + 1)
         }
          
        } catch (error) {
          console.error("Error updating project:", error);
      } 
    }
  }
    

  /**
   * Calculates and returns an array of page numbers for pagination.
   *
   * @returns {number[]} An array of page numbers or ellipses ('...') for navigation.
   */
  const renderPageButtons = () => {
    const totalPages = Math.ceil(totalTables / pageSize);
    const visiblePages = 3;
    const halfVisible = Math.floor(visiblePages / 2);

    if (totalPages <= visiblePages) {
      return Array.from({ length: totalPages }, (_, index) => index + 1);
    } else {
      const startPage = Math.max(1, currentPage - halfVisible);
      const endPage = Math.min(totalPages, startPage + visiblePages - 1);

      const pages = [];
      if (startPage > 1) {
        pages.push(1, "...");
      }
      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }
      if (endPage < totalPages) {
        pages.push("...", totalPages);
      }
      return pages;
    }
  };

  return (
    <>
      <div className="page-row">
        <Sidebar />
        <div className="project-wrapper">
          <Navbar />
          <div className="project-section">
            <Assign
              closeMode={toogleDropDownBoxes}
              authToken={loggedInUser ? loggedInUser.token : ""}
              taskMembers={assignees}
              projectPeople={oneProject.memberList}
              assignModel={dropDownBoxesHeight}
            />
            <div className="container">
              <Comment
                closeComment={toogleDropDownBoxes}
                dropDownValue={dropDownBoxesHeight}
                commentList={taskComments}
              />

              <input
                className="project_title"
                onChange={(e) =>
                  updateProject(
                    e.target.value,
                    oneProject.description,
                    oneProject.sortDirection
                  )
                }
                defaultValue={oneProject.title}
                contentEditable
              />
              {/* <h6 className='page-section-header'>{direction}</h6> */}

              <div className="filter_project_section">
                <div className="page-row project_filter_row">
                  <div className="table-filters">
                    <div className="page-row">
                      <form action="" className="table_filter_col">
                        <i className="lni lni-search"></i>
                        <input
                          type="text"
                          placeholder="Search tables"
                          value={searchTerm}
                          onChange={(e) => setSearchTerm(e.target.value)}
                        />
                      </form>
                      {/* use this function to filter tables, but since then tables only have one fields 'tableName' it is use as default therefore no need to use this function */}

                      {/* <div className='table_filter_by table_filter_col'>
                                            <select value={sortField} onChange={(e) => setSortField(e.target.value)}>
                                            <option value=" ">Sort table by</option>
                                            <option value="tableName">table name</option>
                                            </select>
                                        </div> */}

                      <div className="peron_filter table_filter_col">
                        <i className="lni lni-users"></i>
                        <p
                          onClick={() =>
                            toogleDropDownBoxes("filterBox", 100, 0, "")
                          }
                        >
                          Filter by person
                        </p>
                        <div
                          style={{ height: dropDownBoxesHeight.filterBox }}
                          className="person_filter_box"
                        >
                          <div className="filter_person_wrapper">
                            <h6>Select member to filter with:</h6>
                            {oneProject.memberList.map((name, i) => (
                              <p key={i}>{name.username}</p>
                            ))}
                          </div>
                        </div>
                      </div>
                      <div className="filter_sort table_filter_col">
                        <i className="lni lni-sort-alpha-asc"></i>

                        <select
                          value={direction}
                          onChange={(e) =>
                            updateProject(
                              oneProject.title,
                              oneProject.description,
                              e.target.value
                            )
                          }
                        >
                          <option value="ASC">Ascending</option>
                          <option value="DESC">Descending</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div className="project_create_table_invite">
                    <div className="project_members">
                      {oneProject.memberList.map((name, i) => (
                        <span key={i}>{name.username.charAt(0)}</span>
                      ))}
                    </div>
                    <div className="invite_btn">
                      <p
                        onClick={() =>
                          toogleDropDownBoxes("inviteBox", 250, 0, "")
                        }
                      >
                        invite <i className="lni lni-circle-plus"></i>
                      </p>
                      <div
                        style={{ height: dropDownBoxesHeight.inviteBox }}
                        className="invite_members"
                      >
                        <div className="member_invite_wrapper">
                          <h5>Search members to add by name </h5>
                          <form action="">
                            {" "}
                            <input
                              onChange={(e) => searchforMembers(e.target.value)}
                              type="text"
                              placeholder="Please search member"
                            />
                            <input type="submit" value="search" />
                          </form>
                          {memberFound
                            ? memberFound.map((memberName, i) => (
                                <p
                                  key={i}
                                  onClick={() =>
                                    inviteMember(memberName.user_id)
                                  }
                                >
                                  {memberName.fullName}
                                </p>
                              ))
                            : ""}
                          <h6>List of project members</h6>
                          {oneProject.memberList.map((names, index) => (
                            <p key={index}>{names.username}</p>
                          ))}
                        </div>
                      </div>
                    </div>
                    <button onClick={() => createTable(oneProject.projectId)}>
                      <i className="lni lni-layers"></i>
                      <span>create table</span>
                    </button>
                  </div>
                </div>
              </div>
              {oneProject.tables.map((table, table_index) => (
                <div key={table.tableId} className="table_section">
                      <div className="edit-table">
                           <i className="lni lni-chevron-down" onClick={()=>toogleDropDownBoxes("table", 90,table_index,'tableIndex')}></i>
                            <input
                            id="table_title"
                            onChange={(e) => updateTableName(e.target.value, table)}
                            defaultValue={table.tableName}
                            contentEditable
                            />
                        <div
                      style={{
                        height:
                          table_index == dropDownBoxesHeight.tableIndex
                            ? dropDownBoxesHeight.table
                            : 0,
                      }}
                      className="edit-table-hidden-box">
                      <div
                        className="edit_table_icon"
                        onClick={() =>
                          deleteTable(oneProject.projectId, table.tableId)
                        }
                      >
                        <i className="lni lni-trash-can"></i>
                        <span>Delete table</span>
                      </div>
                      <div className="edit_table_icon">
                        <i className="lni lni-circle-plus"></i>
                        <span onClick={() => Adding_Task(table.tableId)}>
                          Add task
                        </span>
                      </div>
                    </div>
                  </div>
                  <div className="project_table">
                    <div className="page-row col_name_row">
                      <div
                        style={{
                          height:
                            dropDownBoxesHeight.columnIndex == table_index
                              ? dropDownBoxesHeight.addColumn
                              : 0,
                        }}
                        className="add-table-column"
                      >
                        <h6>Add extra table columns:</h6>
                        <p onClick={() => toogleHiddenColumns(table.tableId,table.hiddenTaskColumns,"priority", "show")}>
                          <i className="lni lni-sort-alpha-asc"></i>
                          <span>Priority</span>
                        </p>
                      </div>
                      <span className="field_name col_name">Title</span>
                      <span className="field_name col_name col-description">
                        Description
                      </span>
                      <span className="field_name col_name">Owner</span>
                      <span className="field_name col_name">Assignee</span>
                      <span className="field_name col_name">start-date</span>
                      <span className="field_name col_name">end-date</span>
                      <span className="field_name col_name">status</span>
                      <span style={{ display: table.hiddenTaskColumns.priority == true ? "block" : "none" }} className="field_name col_name"> Priority
                        <i onClick={() => toogleHiddenColumns(table.tableId,table.hiddenTaskColumns,"priority", "hide")} className="fa fa-eye-slash" aria-hidden="true"></i></span>
                      <div
                        className="more  more-col"
                        onClick={() =>
                          toogleDropDownBoxes(
                            "addColumn",
                            120,
                            table_index,
                            "columnIndex"
                          )
                        }
                      >
                        {" "}
                        <i className="lni lni-circle-plus"></i>
                      </div>
                    </div>
                    {table.tasks.map((task, taskIndex) => (
                      <div key={task.task_id}>
                        <div className="page-row table_task_row">
                          <div
                            style={{
                              height:
                                dropDownBoxesHeight.rowId == task.task_id
                                  ? dropDownBoxesHeight.editRow
                                  : 0,
                            }}
                            className="edit_task_box"
                          >
                            <div className="box-arrow"></div>
                            <p
                              onClick={() =>
                                deleteTask(task.task_id, table.tableId)
                              }
                            >
                              <i className="lni lni-trash-can"></i>
                              <span>Delete</span>
                            </p>
                            <p
                              onClick={() => duplicateTask(table.tableId, task)}
                            >
                              <i className="lni lni-clipboard"></i>
                              <span>Duplicate</span>
                            </p>
                          </div>
                          <div className="field_name text_task table-task task-title">
                            <i
                              onClick={() =>
                                toogleDropDownBoxes(
                                  "editRow",
                                  90,
                                  task.task_id,
                                  "rowId"
                                )
                              }
                              className="more-task lni lni-more"
                            ></i>
                            <input
                              onBlur={(e) =>
                                sendEditedRow(task, e.target.value, "title")
                              }
                              defaultValue={task.title}
                            />
                          </div>
                          <div className="field_name table-task text_task row_description">
                            <input
                              defaultValue={task.description}
                              onBlur={(e) =>
                                sendEditedRow(
                                  task,
                                  e.target.value,
                                  "description"
                                )
                              }
                            />
                            <i
                              onClick={() =>
                                loadComments(task.comments, task.task_id)
                              }
                              className="lni lni-comments-alt-2"
                            ></i>
                          </div>
                          <div className="field_name table-task text_task">
                            {task.username}
                          </div>
                          <div className="assignee field_name table-task text_task">
                            {task.assignees.length > 0 ? (
                              task.assignees.map((taskAssignee, i) => (
                                <span key={i} className="task_assign_letter">
                                  {taskAssignee.username.charAt(0)}
                                </span>
                              ))
                            ) : (
                              <span>invite</span>
                            )}
                            <i
                              className="lni lni-circle-plus"
                              onClick={() =>
                                openAssignModel(task.assignees, task.task_id)
                              }
                            ></i>
                          </div>
                          <div className="field_name table-task">
                            <input
                              defaultValue={task.start_date}
                              onBlur={(e) =>
                                sendEditedRow(
                                  task,
                                  e.target.value,
                                  "start_date"
                                )
                              }
                              type="date"
                            />
                          </div>
                          <div className="field_name table-task">
                            <input defaultValue={task.end_date} type="date" onBlur={(e) => sendEditedRow(task, e.target.value, "end_date")}/>
                          </div>
                          <div onClick={() => toogleDropDownBoxes( "statusBox",160,task.task_id,"statusIndex")} id={task.status} className="status field_name table-task">
                            {task.status}
                            <div style={{height: dropDownBoxesHeight.statusIndex == task.task_id ? dropDownBoxesHeight.statusBox: 0}} className="status_dropdown">
                              <div className="status_dropdown_content">
                                    <span onClick={(e)=>sendEditedRow(task, e.target.innerText,'status')} id='DONE'>DONE</span>
                                    <span onClick={(e)=>sendEditedRow(task, e.target.innerText,'status')} id='TODO'>TODO</span>
                                    <span onClick={(e)=>sendEditedRow(task, e.target.innerText,'status')} id='INPROGRESS'>INPROGRESS</span>
                                </div>
                            </div>
                          </div>
                          <div onClick={() => toogleDropDownBoxes( "priorityBox",140,task.task_id,"priorityIndex")} style={{display:table.hiddenTaskColumns.priority == true ?"block":"none" }} id={task.priority} className="priority field_name table-task">
                            {task.priority}
                            <div className="priority_dropdown_content" style={{height: dropDownBoxesHeight.priorityIndex == task.task_id ? dropDownBoxesHeight.priorityBox: 0}}>
                              <span onClick={(e)=>sendEditedRow(task, e.target.innerText,'priority')} id="HIGH">HIGH</span>
                              <span onClick={(e)=>sendEditedRow(task, e.target.innerText,'priority')} id="LOW">LOW</span>
                              <span onClick={(e)=>sendEditedRow(task, e.target.innerText,'priority')} id="MEDIUM">MEDIUM</span>
                            </div>
                          </div>
                          <div className="more"></div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
            <>
              <div className="pagination-container">
                <ul className="pagination">
                  <li>
                    <button
                      className="nav-button"
                      onClick={() => goToPage(currentPage - 1)}
                      disabled={currentPage === 1}
                    >
                      <img src={previousIcon} alt="Previous" />
                    </button>
                  </li>
                  {renderPageButtons().map((page, index) => (
                    <li key={index}>
                      {typeof page === "number" ? (
                        <button
                          onClick={() => goToPage(page)}
                          className={
                            currentPage === page ? "active" : "inactive"
                          }
                        >
                          {page}
                        </button>
                      ) : (
                        <span>{page}</span>
                      )}
                    </li>
                  ))}
                  <li>
                    <button
                      className="nav-button"
                      onClick={() => goToPage(currentPage + 1)}
                      disabled={
                        currentPage === Math.ceil(totalTables / pageSize)
                      }
                    >
                      <img src={nextIcon} alt="Next" />
                    </button>
                  </li>
                </ul>
              </div>
            </>
          </div>
        </div>
      </div>
    </>
  );
}

export default ProjectSection;
