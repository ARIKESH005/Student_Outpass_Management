const API_URL = 'https://student-outpass-management-1.onrender.com/api';

// Setup common fetch headers
function getHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    };
}

// Common Toast logic
const Toast = typeof Swal !== 'undefined' ? Swal.mixin({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true,
    background: 'var(--bg-card)',
    color: 'var(--text-main)'
}) : null;

function showToast(type, message) {
    if (Toast) {
        Toast.fire({ icon: type, title: message });
    } else {
        alert(message);
    }
}

// Check Authentication
function checkAuth(requiredRole) {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    
    if (!token || !role) {
        window.location.href = 'index.html';
        return false;
    }
    
    if (requiredRole && role !== requiredRole) {
        if (role === 'STUDENT') window.location.href = 'student-dashboard.html';
        else if (role === 'ADMIN') window.location.href = 'admin-dashboard.html';
        return false;
    }
    
    const usernameDisplay = document.getElementById('username-display');
    if (usernameDisplay) {
        usernameDisplay.textContent = localStorage.getItem('username') || role;
    }
    
    return true;
}

function logout() {
    Swal.fire({
        title: 'Ready to leave?',
        text: 'You will be logged out of your current session.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: 'var(--danger)',
        cancelButtonColor: 'var(--secondary)',
        confirmButtonText: 'Yes, logout'
    }).then((result) => {
        if (result.isConfirmed) {
            localStorage.removeItem('token');
            localStorage.removeItem('role');
            localStorage.removeItem('username');
            window.location.href = 'index.html';
        }
    });
}

function deleteAccount() {
    Swal.fire({
        title: 'Delete Account?',
        text: 'This action is irreversible. All your outpass requests will also be deleted.',
        icon: 'error',
        showCancelButton: true,
        confirmButtonColor: 'var(--danger)',
        cancelButtonColor: 'var(--secondary)',
        confirmButtonText: 'Yes, delete it!'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${API_URL}/student/account`, {
                    method: 'DELETE',
                    headers: getHeaders()
                });
                if (response.ok) {
                    Swal.fire('Deleted!', 'Your account has been deleted.', 'success').then(() => {
                        localStorage.removeItem('token');
                        localStorage.removeItem('role');
                        localStorage.removeItem('username');
                        window.location.href = 'index.html';
                    });
                } else {
                    showToast('error', 'Failed to delete account');
                }
            } catch (error) {
                showToast('error', 'Server connection error');
            }
        }
    });
}

// Auth API Calls
async function handleLogin(e) {
    e.preventDefault();
    
    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({
                username: document.getElementById('login-username').value,
                password: document.getElementById('login-password').value
            })
        });

        const data = await response.json();
        
        if (response.ok) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('role', data.role);
            localStorage.setItem('username', data.username);
            
            showToast('success', 'Logged in successfully');
            setTimeout(() => {
                if (data.role === 'STUDENT') window.location.href = 'student-dashboard.html';
                else if (data.role === 'ADMIN') window.location.href = 'admin-dashboard.html';
            }, 1000);
        } else {
            showToast('error', data.error || 'Invalid credentials');
        }
    } catch (error) {
        showToast('error', 'Server connection error');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    
    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({
                username: document.getElementById('reg-username').value,
                email: document.getElementById('reg-email').value,
                department: document.getElementById('reg-department').value,
                password: document.getElementById('reg-password').value
            })
        });

        if (response.ok) {
            showToast('success', 'Account created! Please login.');
            setTimeout(toggleAuthMode, 2000);
        } else {
            const data = await response.json();
            showToast('error', data.error || 'Registration failed');
        }
    } catch (error) {
        showToast('error', 'Server connection error');
    }
}

// Student API Calls
async function applyOutpass(e) {
    e.preventDefault();
    
    try {
        const response = await fetch(`${API_URL}/student/apply`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({
                reason: document.getElementById('reason').value,
                outDate: document.getElementById('outDate').value,
                returnDate: document.getElementById('returnDate').value,
                parentPhone: document.getElementById('parentPhone').value
            })
        });

        if (response.ok) {
            showToast('success', 'Outpass request submitted successfully!');
            e.target.reset();
        } else {
            showToast('error', 'Failed to submit outpass');
        }
    } catch (error) {
        showToast('error', 'Server connection error');
    }
}

async function loadMyRequests() {
    try {
        const response = await fetch(`${API_URL}/student/myrequests`, {
            headers: getHeaders()
        });
        
        if (response.ok) {
            const data = await response.json();
            renderTable(data, 'my-requests-body', false);
        }
    } catch (error) {
        showToast('error', 'Failed to load requests');
    }
}

// Admin API Calls
let globalRequests = []; // Store for filtering

async function loadAllRequests() {
    try {
        const response = await fetch(`${API_URL}/admin/requests`, {
            headers: getHeaders()
        });
        
        if (response.ok) {
            globalRequests = await response.json();
            renderTable(globalRequests, 'admin-requests-body', true);
            updateAnalytics(globalRequests);
            if(typeof renderChart === 'function') renderChart(globalRequests);
        }
    } catch (error) {
        showToast('error', 'Failed to load requests');
    }
}

function filterTable() {
    const query = document.getElementById('search-input').value.toLowerCase();
    const filtered = globalRequests.filter(req => 
        req.studentName.toLowerCase().includes(query) || 
        req.reason.toLowerCase().includes(query)
    );
    renderTable(filtered, 'admin-requests-body', true);
}

function updateAnalytics(data) {
    const total = data.length;
    const pending = data.filter(r => r.status === 'PENDING').length;
    const approved = data.filter(r => r.status === 'APPROVED').length;
    const rejected = data.filter(r => r.status === 'REJECTED').length;

    if(document.getElementById('kpi-total')) document.getElementById('kpi-total').textContent = total;
    if(document.getElementById('kpi-pending')) document.getElementById('kpi-pending').textContent = pending;
    if(document.getElementById('kpi-approved')) document.getElementById('kpi-approved').textContent = approved;
    if(document.getElementById('kpi-rejected')) document.getElementById('kpi-rejected').textContent = rejected;
}

function confirmAction(id, action) {
    const isApprove = action === 'approve';
    Swal.fire({
        title: isApprove ? 'Approve Outpass?' : 'Reject Outpass?',
        text: isApprove ? "Student will be allowed to leave." : "This action cannot be undone.",
        icon: isApprove ? 'question' : 'warning',
        showCancelButton: true,
        confirmButtonColor: isApprove ? 'var(--success)' : 'var(--danger)',
        cancelButtonColor: 'var(--secondary)',
        confirmButtonText: isApprove ? 'Yes, approve' : 'Yes, reject'
    }).then((result) => {
        if (result.isConfirmed) {
            handleAction(id, action);
        }
    });
}

async function handleAction(id, action) {
    try {
        const response = await fetch(`${API_URL}/admin/${action}/${id}`, {
            method: 'PUT',
            headers: getHeaders()
        });
        
        if (response.ok) {
            showToast('success', `Request ${action}d successfully`);
            loadAllRequests();
        } else {
            showToast('error', 'Action failed. Unauthorized or error.');
        }
    } catch (error) {
        showToast('error', 'Server error during action');
    }
}

function showQrCode(qrCodeId) {
    if(!qrCodeId) {
        showToast('error', 'QR Code not available yet.');
        return;
    }
    
    // Construct the public scanner URL using the live production backend API
    const publicScannerUrl = `${API_URL}/public/scan/${qrCodeId}`;
    
    Swal.fire({
        title: 'Your Gate Pass',
        html: `
            <p style="margin-bottom: 1rem; color: var(--text-muted);">Show this QR code to the gate security to scan.</p>
            <div id="qrcode-container" style="display:flex; justify-content:center; margin: 1rem 0; padding: 1rem; background: white; border-radius: 0.5rem; display:inline-block;"></div>
            <p style="font-size: 0.75rem; color: var(--danger); margin-top: 1rem;">Note: This QR code can only be scanned ONCE.</p>
        `,
        showConfirmButton: true,
        confirmButtonText: 'Close',
        confirmButtonColor: 'var(--primary)',
        didOpen: () => {
            // Render the actual QR Code inside the sweetalert modal
            new QRCode(document.getElementById("qrcode-container"), {
                text: publicScannerUrl,
                width: 200,
                height: 200,
                colorDark : "#000000",
                colorLight : "#ffffff",
                correctLevel : QRCode.CorrectLevel.H
            });
        }
    });
}

// Helper to render tables
function renderTable(data, tbodyId, isAdmin) {
    const tbody = document.getElementById(tbodyId);
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    if (data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="${isAdmin ? '8' : '6'}" style="text-align:center; color:var(--text-muted); padding:2rem;">No requests found.</td></tr>`;
        return;
    }
    
    data.forEach(req => {
        let statusBadge = '';
        if (req.status === 'APPROVED' && req.gateScanned) {
             statusBadge = `<span class="badge" style="background: rgba(107, 114, 128, 0.15); color: var(--text-muted);"><i class="fa-solid fa-door-open" style="font-size:0.5rem; margin-right:4px;"></i> USED AT GATE</span>`;
        } else {
             statusBadge = `<span class="badge badge-${req.status.toLowerCase()}"><i class="fa-solid fa-circle" style="font-size:0.5rem; margin-right:4px;"></i> ${req.status}</span>`;
        }
        
        let actions = '';
        if (isAdmin && req.status === 'PENDING') {
            actions = `
                <button class="action-btn btn-approve" onclick="confirmAction(${req.id}, 'approve')"><i class="fa-solid fa-check"></i></button>
                <button class="action-btn btn-reject" onclick="confirmAction(${req.id}, 'reject')"><i class="fa-solid fa-xmark"></i></button>
            `;
        } else if (!isAdmin && req.status === 'APPROVED' && !req.gateScanned) {
            // Show QR button for students if approved and not yet scanned at the gate
            actions = `
                <button class="action-btn" style="background: var(--primary); color: white;" onclick="showQrCode('${req.qrCodeId}')">
                    <i class="fa-solid fa-qrcode"></i> View QR
                </button>
            `;
        }
        
        const tr = document.createElement('tr');
        tr.innerHTML = `
            ${isAdmin ? `<td><strong>${req.studentName}</strong></td><td>${req.department}</td>` : ''}
            <td>${req.reason}</td>
            <td>${req.outDate}</td>
            <td>${req.returnDate}</td>
            <td>${req.parentPhone}</td>
            <td>${statusBadge}</td>
            <td>${actions}</td>
        `;
        tbody.appendChild(tr);
    });
}

// Theme Toggle
function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
    
    // update icon
    const icon = document.getElementById('theme-icon');
    if(icon) {
        icon.className = newTheme === 'dark' ? 'fa-solid fa-sun' : 'fa-solid fa-moon';
    }
}
