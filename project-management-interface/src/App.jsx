import CreateProject from "./Component/CreateProject";
import Guide from "./Component/Guide";
import ProjectSection from "./Component/ProjectSection";
import "./Component/styles/stylesheet.css";
import Login from "./Component/Login";
import Users from "./Component/Users";
import PrivateRoutes from "./Component/PrivateRoutes";
import AccountDeactivate from "./Component/AccountDeactivate";
import Privacy from "./Component/Privacy";
import Home from "./Component/Home";
import FeedbackAdmin from "./Component/FeedbackAdmin";
import FeedbackPage from "./Component/FeedbackPage";
import SettingsPage from "./Component/SettingsPage";
import ProfileEditPage from "./Component/ProfileEditPage";
import ProjectStats from "./Component/ProjectStats"
import SystemUsage from "./Component/SystemUsage"
import NotificationSettings from "./Component/NotificationSettings";
import OAuth2Login from "./Component/Oauth2Login";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

function App() {
  return (
    <Router>
      <Routes>
        <Route element={<PrivateRoutes />}>
          <Route element={<Users />} path="/users" exact />
          <Route element={<Guide />} path="/help" exact />
          <Route element={<CreateProject />} path="/createProject" exact />
          <Route element={<ProjectSection />} path="/project" exact />
          <Route element={<ProjectStats />} path="/projectStats" exact />
          <Route element={<AccountDeactivate />} path="/disable" exact />
          <Route element={<SystemUsage />} path="/systemUsage" exact />     
          <Route element={<Home />} path="/" exact />
          <Route path="/feedbackAdmin" element={<FeedbackAdmin />} />
          <Route path="/profileEditPage" element={<ProfileEditPage />} />
          <Route path="/settingspage" element={<SettingsPage />} />
          <Route path="/notificationsettings" element={<NotificationSettings />}/>
          <Route path="/feedbackpage" element={<FeedbackPage />} />
        </Route>
        <Route path="/login" element={<Login />} />
        <Route path="/privacy" element={<Privacy />} />
        <Route path="/oauth2login" element={<OAuth2Login />} />
      </Routes>
    </Router>
  );
}

export default App;
