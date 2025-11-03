import { Link } from 'react-router';

const Navbar = () => {
    return (
        <nav style={{ display: 'flex', justifyContent: 'space-between', padding: '1rem', backgroundColor: '#eee' }}>
            <div>
                <Link to="/projects" style={{ marginRight: '1rem' }}>Projekty (Mnou spravované projekty)</Link>
                <Link to="/assigned-tickets" style={{ marginRight: '1rem' }}>Mně přiřazené tickety</Link>
                <Link to="/elk-ticket-search">ELK vyhledání ticketů</Link>
            </div>
            <div>
                <Link to="/account">Můj účet</Link>
            </div>
        </nav>
    );
};

export default Navbar;
