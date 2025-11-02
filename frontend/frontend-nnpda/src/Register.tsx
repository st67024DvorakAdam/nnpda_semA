import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import axios from 'axios';

const Register: React.FC = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/auth/register', {
                username,
                email,
                password
            });

            // response.data obsahuje nově vytvořeného uživatele
            console.log('Registrován uživatel:', response.data);
            alert('Registrace proběhla úspěšně! Přihlaste se.');

            // přesměrování na login stránku
            navigate('/login');
        } catch (error: any) {
            console.error(error);
            // zkusíme zobrazit chybovou zprávu z backendu, pokud existuje
            const message = error.response?.data?.message || 'Chyba při registraci';
            alert(message);
        }
    };

    return (
        <div className="register-container">
            <h2>Registrace</h2>
            <form onSubmit={handleRegister}>
                <div>
                    <label htmlFor="username">Username:</label>
                    <input
                        id="username"
                        type="text"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="email">Email:</label>
                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="password">Heslo:</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                        minLength={6}
                    />
                </div>

                <button type="submit">Registrovat se</button>
            </form>
        </div>
    );
};

export default Register;
