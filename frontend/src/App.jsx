import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import CreateRideRequest from "./pages/CreateRideRequest";
import PendingRequests from "./pages/PendingRequests";
import MyRides from "./pages/MyRides";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/create-request" element={<CreateRideRequest />} />
        <Route path="/pending-requests" element={<PendingRequests />} />
        <Route path="/my-rides" element={<MyRides />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
