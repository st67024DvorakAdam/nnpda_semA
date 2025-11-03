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

const API_BASE = "http://localhost:8080";

const ElkTicketsSearch: React.FC = () => {
    const [tickets, setTickets] = useState<TicketDocument[]>([]);

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
    }, []);

    const getAuthHeader = () => {
        const token = localStorage.getItem("token");
        return { Authorization: `Bearer ${token}` };
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

                {/* Filtry */}
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

                    <button
                        onClick={applyFilter}
                        className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded"
                    >
                        Aplikovat filtr
                    </button>
                </div>

                {/* Tabulka */}
                <div className="overflow-x-auto">
                    <table className="min-w-full border border-gray-200 text-left">
                        <thead className="bg-gray-100">
                        <tr>
                            <th className="px-4 py-2 border w-16">ID</th>
                            <th className="px-4 py-2 border w-64">Název</th>
                            <th className="px-4 py-2 border w-32">Stav</th>
                            <th className="px-4 py-2 border w-32">Priorita</th>
                            <th className="px-4 py-2 border w-32">Typ</th>
                        </tr>
                        </thead>
                        <tbody>
                        {tickets.length === 0 ? (
                            <tr>
                                <td colSpan={5} className="px-4 py-2 text-center text-gray-600">
                                    Žádné tickety k zobrazení.
                                </td>
                            </tr>
                        ) : (
                            tickets.map((ticket) => (
                                <tr key={ticket.id} className="hover:bg-gray-50">
                                    <td className="px-4 py-2 border">{ticket.id}</td>
                                    <td className="px-4 py-2 border">{ticket.title || "—"}</td>
                                    <td className="px-4 py-2 border">{ticket.state || "—"}</td>
                                    <td className="px-4 py-2 border">{ticket.priority || "—"}</td>
                                    <td className="px-4 py-2 border">{ticket.type || "—"}</td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default ElkTicketsSearch;
