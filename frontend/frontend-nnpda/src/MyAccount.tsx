import React, {useState } from "react";
import axios from "axios";
import Navbar from "./Navbar.tsx";

const API_BASE = "http://localhost:8080";

const MyAccount: React.FC = () => {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [newPasswordRepeat, setNewPasswordRepeat] = useState("");
    const [message, setMessage] = useState("");

    const getAuthHeader = () => {
        const token = localStorage.getItem("token");
        return { Authorization: `Bearer ${token}` };
    };

    const handleChangePassword = async () => {
        if (newPassword !== newPasswordRepeat) {
            setMessage("Nové heslo se neshoduje.");
            return;
        }

        try {
            await axios.post(
                `${API_BASE}/auth/change-password`,
                { oldPassword, newPassword },
                { headers: getAuthHeader() }
            );
            setMessage("Heslo bylo úspěšně změněno.");
            setOldPassword("");
            setNewPassword("");
            setNewPasswordRepeat("");
        } catch (error) {
            console.error(error);
            setMessage("Chyba při změně hesla.");
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <Navbar />
            <div className="p-6 max-w-md mx-auto">
                <h2 className="text-2xl font-bold mb-4">Můj účet</h2>

                <h3 className="text-xl font-semibold mb-2">Změna hesla</h3>
                <div className="flex flex-col gap-3 mb-4">
                    <input
                        type="password"
                        placeholder="Staré heslo"
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        className="border p-2 rounded w-full"
                    />
                    <input
                        type="password"
                        placeholder="Nové heslo"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        className="border p-2 rounded w-full"
                    />
                    <input
                        type="password"
                        placeholder="Nové heslo znovu"
                        value={newPasswordRepeat}
                        onChange={(e) => setNewPasswordRepeat(e.target.value)}
                        className="border p-2 rounded w-full"
                    />
                    <button
                        onClick={handleChangePassword}
                        className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded"
                    >
                        Změnit heslo
                    </button>
                    {message && <p className="text-sm text-red-600 mt-2">{message}</p>}
                </div>
            </div>
        </div>
    );
};

export default MyAccount;
