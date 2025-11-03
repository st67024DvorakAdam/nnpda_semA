import React, { useEffect, useState } from "react";
import axios from "axios";
import Navbar from "./Navbar.tsx";

interface TicketDocument {
    id: number;
    title: string;
    state: string;
    priority: string;
    type: string;
    projectId: number;
    projectName: string;
    assigneeId: number;
    assigneeUsername: string;
}

interface AppUserDto {
    id: number;
    username: string;
    email: string;
}

const API_BASE = "http://localhost:8080";

const ElkTicketsSearch: React.FC = () => {
    const [tickets, setTickets] = useState<TicketDocument[]>([]);
    const [users, setUsers] = useState<AppUserDto[]>([]);

    const [filters, setFilters] = useState({
        title: "",
        state: "",
        priority: "",
        type: "",
        assigneeUsername: "",
    });

    const priorities = ["", "LOW", "MED", "HIGH"];
    const states = ["", "OPEN", "IN_PROGRESS", "DONE"];
    const types = ["", "BUG", "FEATURE", "TASK"];

    useEffect(() => {
        fetchAllTickets();
        fetchUsers();
    }, []);

    const getAuthHeader = () => {
        const token = localStorage.getItem("token");
        return { Authorization: `Bearer ${token}` };
    };

    const fetchUsers = async () => {
        try {
            const response = await axios.get(`${API_BASE}/users`, {
                headers: getAuthHeader(),
            });
            if (Array.isArray(response.data)) {
                setUsers(response.data);
            } else {
                console.warn("Uživatelé nejsou pole:", response.data);
                setUsers([]);
            }
        } catch (error) {
            console.error("Chyba při načítání uživatelů:", error);
        }
    };

    const fetchAllTickets = async () => {
        try {
            const response = await axios.get(`${API_BASE}/elastic/tickets`, {
                headers: getAuthHeader(),
            });
            if (Array.isArray(response.data)) {
                setTickets(response.data);
            } else {
                console.warn("Tickety nejsou pole:", response.data);
                setTickets([]);
            }
        } catch (error) {
            console.error("Chyba při načítání ticketů:", error);
            setTickets([]);
        }
    };

    const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        setFilters({
            ...filters,
            [e.target.name]: e.target.value,
        });
    };

    const applyFilter = async () => {
        try {
            if (
                !filters.title &&
                !filters.state &&
                !filters.priority &&
                !filters.type &&
                !filters.assigneeUsername
            ) {
                fetchAllTickets();
                return;
            }

            const requests: Promise<TicketDocument[]>[] = [];

            if (filters.title)
                requests.push(
                    axios
                        .get(`${API_BASE}/elastic/tickets/search/title?keyword=${filters.title}`, {
                            headers: getAuthHeader(),
                        })
                        .then((r) => r.data)
                );
            if (filters.state)
                requests.push(
                    axios
                        .get(`${API_BASE}/elastic/tickets/filter/state?state=${filters.state}`, {
                            headers: getAuthHeader(),
                        })
                        .then((r) => r.data)
                );
            if (filters.priority)
                requests.push(
                    axios
                        .get(`${API_BASE}/elastic/tickets/filter/priority?priority=${filters.priority}`, {
                            headers: getAuthHeader(),
                        })
                        .then((r) => r.data)
                );
            if (filters.type)
                requests.push(
                    axios
                        .get(`${API_BASE}/elastic/tickets/filter/type?type=${filters.type}`, {
                            headers: getAuthHeader(),
                        })
                        .then((r) => r.data)
                );
            if (filters.assigneeUsername)
                requests.push(
                    axios
                        .get(`${API_BASE}/elastic/tickets/filter/assignee?username=${filters.assigneeUsername}`, {
                            headers: getAuthHeader(),
                        })
                        .then((r) => r.data)
                );

            const responses = await Promise.all(requests);
            const validResponses = responses.filter(Array.isArray);

            const results =
                validResponses.length > 0
                    ? validResponses.reduce((acc, curr) =>
                        acc.filter((item) => curr.some((c) => c.id === item.id))
                    )
                    : [];

            setTickets(results);
        } catch (error) {
            console.error("Chyba při filtrování:", error);
            setTickets([]);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <Navbar />

            <div className="p-6">
                <h2 className="text-2xl font-bold mb-4">ELK Vyhledání Ticketů</h2>

                <div className="grid grid-cols-1 md:grid-cols-6 gap-4 mb-6">
                    <input
                        type="text"
                        name="title"
                        placeholder="Název"
                        value={filters.title}
                        onChange={handleFilterChange}
                        className="border p-2 rounded w-full"
                    />

                    <select
                        name="state"
                        value={filters.state}
                        onChange={handleFilterChange}
                        className="border p-2 rounded w-full"
                    >
                        {states.map((s) => (
                            <option key={s} value={s}>
                                {s || "— Stav —"}
                            </option>
                        ))}
                    </select>

                    <select
                        name="priority"
                        value={filters.priority}
                        onChange={handleFilterChange}
                        className="border p-2 rounded w-full"
                    >
                        {priorities.map((p) => (
                            <option key={p} value={p}>
                                {p || "— Priorita —"}
                            </option>
                        ))}
                    </select>

                    <select
                        name="type"
                        value={filters.type}
                        onChange={handleFilterChange}
                        className="border p-2 rounded w-full"
                    >
                        {types.map((t) => (
                            <option key={t} value={t}>
                                {t || "— Typ —"}
                            </option>
                        ))}
                    </select>

                    <select
                        name="assigneeUsername"
                        value={filters.assigneeUsername}
                        onChange={handleFilterChange}
                        className="border p-2 rounded w-full"
                    >
                        <option value="">— Přiřazený —</option>
                        {users.map((u) => (
                            <option key={u.id} value={u.username}>
                                {u.username}
                            </option>
                        ))}
                    </select>

                    <button
                        onClick={applyFilter}
                        className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded"
                    >
                        Aplikovat filtr
                    </button>
                </div>

                {!Array.isArray(tickets) || tickets.length === 0 ? (
                    <p className="text-gray-600">Žádné tickety k zobrazení.</p>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {tickets.map((ticket) => (
                            <div key={ticket.id} className="border rounded-lg p-4 shadow-sm bg-white">
                                <h2 className="text-lg font-semibold">{ticket.title}</h2>
                                <p className="text-sm text-gray-600">Projekt: {ticket.projectName}</p>
                                <p className="text-sm">Stav: {ticket.state}</p>
                                <p className="text-sm">Priorita: {ticket.priority}</p>
                                <p className="text-sm">Typ: {ticket.type}</p>
                                <p className="text-sm">
                                    Přiřazeno: {ticket.assigneeUsername || "—"}
                                </p>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ElkTicketsSearch;
