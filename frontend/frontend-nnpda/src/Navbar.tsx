import { Link, useNavigate } from 'react-router';

const Navbar = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token"); // smaže JWT
        navigate("/login"); // přesměruje na login
    };

    return (
        <nav style={{ display: 'flex', justifyContent: 'space-between', padding: '1rem', backgroundColor: '#eee' }}>
            <div>
                <Link to="/projects" style={{ marginRight: '1rem' }}>Projekty (Mnou spravované projekty)</Link>
                <Link to="/assigned-tickets" style={{ marginRight: '1rem' }}>Mně přiřazené tickety</Link>
                <Link to="/elk-ticket-search">ELK vyhledání ticketů</Link>
            </div>
            <div>
                <Link to="/account" style={{ marginRight: '1rem' }}>Můj účet</Link>
                <button onClick={handleLogout} style={{ cursor: 'pointer', background: 'none', border: 'none', color: 'blue', textDecoration: 'underline' }}>
                    Odhlásit
                </button>
            </div>
        </nav>
    );
};

export default Navbar;
