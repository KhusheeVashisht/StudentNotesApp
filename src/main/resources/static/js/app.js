const baseUrl = '/api';

function handleApiError(response) {
    if (!response.ok) {
        return response.json().then(error => {
            throw error;
        });
    }
    return response.json();
}

function getToken() {
    return localStorage.getItem('notes_jwt_token');
}

function setToken(token) {
    localStorage.setItem('notes_jwt_token', token);
}

function clearToken() {
    localStorage.removeItem('notes_jwt_token');
}

function authHeaders() {
    const token = getToken();
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

function showMessage(message, isError = false) {
    const alertContainer = document.querySelector('#alert');
    if (!alertContainer) return;
    alertContainer.textContent = message;
    alertContainer.style.display = 'block';
    alertContainer.style.background = isError ? '#f8d7da' : '#dbeafe';
    alertContainer.style.color = isError ? '#842029' : '#1e40af';
}

function hideMessage() {
    const alertContainer = document.querySelector('#alert');
    if (alertContainer) {
        alertContainer.style.display = 'none';
    }
}

async function registerUser(event) {
    event.preventDefault();
    hideMessage();
    const username = document.querySelector('#username').value.trim();
    const email = document.querySelector('#email').value.trim();
    const password = document.querySelector('#password').value;

    try {
        const response = await fetch(`${baseUrl}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });
        const data = await handleApiError(response);
        showMessage(data.message || 'Registration successful.');
        setTimeout(() => window.location.href = '/login.html', 1200);
    } catch (error) {
        const message = error.message || JSON.stringify(error);
        showMessage(message, true);
    }
}

async function loginUser(event) {
    event.preventDefault();
    hideMessage();
    const username = document.querySelector('#username').value.trim();
    const password = document.querySelector('#password').value;

    try {
        const response = await fetch(`${baseUrl}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const data = await handleApiError(response);
        setToken(data.token);
        window.location.href = '/dashboard.html';
    } catch (error) {
        const message = error.message || (error.message ? error.message : JSON.stringify(error));
        showMessage(message, true);
    }
}

async function loadDashboard() {
    const token = getToken();
    if (!token) {
        window.location.href = '/login.html';
        return;
    }
    hideMessage();
    try {
        const response = await fetch(`${baseUrl}/notes`, {
            headers: {
                'Content-Type': 'application/json',
                ...authHeaders()
            }
        });
        const notes = await handleApiError(response);
        renderNoteList(notes);
    } catch (error) {
        clearToken();
        window.location.href = '/login.html';
    }
}

function renderNoteList(notes) {
    const listContainer = document.querySelector('#notesList');
    if (!listContainer) return;
    if (!notes.length) {
        listContainer.innerHTML = '<p class="small-text">No notes yet. Use the form to create your first note.</p>';
        return;
    }
    listContainer.innerHTML = notes.map(note => `
        <div class="note-card" data-id="${note.id}">
            <h3>${note.title}</h3>
            <p>${note.content}</p>
            <p class="small-text">Created: ${new Date(note.createdAt).toLocaleString()}</p>
            <div class="note-actions">
                <button class="button-secondary" onclick="populateEdit(${note.id}, '${escapeHtml(note.title)}', '${escapeHtml(note.content)}')">Edit</button>
                <button onclick="deleteNote(${note.id})">Delete</button>
            </div>
        </div>
    `).join('');
}

function escapeHtml(value) {
    return value.replace(/'/g, "\\'").replace(/\"/g, '\\"');
}

async function createNote(event) {
    event.preventDefault();
    hideMessage();
    const title = document.querySelector('#noteTitle').value.trim();
    const content = document.querySelector('#noteContent').value.trim();

    try {
        await fetch(`${baseUrl}/notes`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...authHeaders()
            },
            body: JSON.stringify({ title, content })
        }).then(handleApiError);
        document.querySelector('#noteTitle').value = '';
        document.querySelector('#noteContent').value = '';
        loadDashboard();
    } catch (error) {
        showMessage(error.message || JSON.stringify(error), true);
    }
}

function populateEdit(id, title, content) {
    document.querySelector('#editSection').style.display = 'block';
    document.querySelector('#editNoteId').value = id;
    document.querySelector('#editTitle').value = title;
    document.querySelector('#editContent').value = content;
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function saveEdit(event) {
    event.preventDefault();
    hideMessage();
    const id = document.querySelector('#editNoteId').value;
    const title = document.querySelector('#editTitle').value.trim();
    const content = document.querySelector('#editContent').value.trim();

    try {
        await fetch(`${baseUrl}/notes/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...authHeaders()
            },
            body: JSON.stringify({ title, content })
        }).then(handleApiError);
        document.querySelector('#editSection').style.display = 'none';
        loadDashboard();
    } catch (error) {
        showMessage(error.message || JSON.stringify(error), true);
    }
}

async function deleteNote(id) {
    if (!confirm('Delete this note?')) return;
    hideMessage();
    try {
        await fetch(`${baseUrl}/notes/${id}`, {
            method: 'DELETE',
            headers: authHeaders()
        }).then(handleApiError);
        loadDashboard();
    } catch (error) {
        showMessage(error.message || JSON.stringify(error), true);
    }
}

function logout() {
    clearToken();
    window.location.href = '/login.html';
}

if (document.querySelector('#registerForm')) {
    document.querySelector('#registerForm').addEventListener('submit', registerUser);
}

if (document.querySelector('#loginForm')) {
    document.querySelector('#loginForm').addEventListener('submit', loginUser);
}

if (document.querySelector('#createNoteForm')) {
    document.querySelector('#createNoteForm').addEventListener('submit', createNote);
    document.querySelector('#editNoteForm').addEventListener('submit', saveEdit);
    document.querySelector('#logoutButton').addEventListener('click', logout);
    loadDashboard();
}
