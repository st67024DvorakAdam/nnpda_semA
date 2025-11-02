import React, { useState } from 'react'
import { useNavigate } from 'react-router'
import axios from 'axios'

const Login: React.FC = () => {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const navigate = useNavigate()

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault()
        try {
            const response = await axios.post('http://localhost:8080/auth/login', {
                username,
                password
            })

            // JWT token je ve response.data.token (nebo jak backend vrací)
            const token = response.data.token || response.data
            localStorage.setItem('token', token)

            // Nastavení tokenu do default hlaviček Axiosu pro další requesty
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`

            // přesměrování na projects stránku
            navigate('/projects')
        } catch (error) {
            console.error(error)
            alert('Chyba přihlášení: špatné uživatelské jméno nebo heslo')
        }
    }

    return (
        <div className="login-container">
            <h2>Přihlášení</h2>
            <form onSubmit={handleLogin}>
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
                    <label htmlFor="password">Heslo:</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                    />
                </div>

                <button type="submit">Přihlásit se</button>
            </form>
        </div>
    )
}

export default Login
