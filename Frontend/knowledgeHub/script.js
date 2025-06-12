document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8000';

    const authView = document.getElementById('authView');
    const mainView = document.getElementById('mainView');
    const rightColumn = document.getElementById('rightColumn');
    const userInfo = document.getElementById('userInfo');
    const usernameDisplay = document.getElementById('usernameDisplay');
    const logoutButton = document.getElementById('logoutButton');
    const notesListEl = document.getElementById('notesList');
    const showSignUpBtn = document.getElementById('showSignUpBtn');
    const showSignInBtn = document.getElementById('showSignInBtn');
    const signInCard = document.getElementById('signInForm').parentElement;
    const signUpCard = document.getElementById('signUpCard');
    const showCreateNoteBtn = document.getElementById('showCreateNoteBtn');
    const createNoteTemplate = document.getElementById('createNoteTemplate');
    const editNoteTemplate = document.getElementById('editNoteTemplate');
    const homeLink = document.getElementById('home-link');
    const publicNotesList = document.getElementById('public-notes-list');
    const publicSearchForm = document.getElementById('public-search-form');
    const publicSearchInput = document.getElementById('public-search-input');
    const showCreateTagBtn = document.getElementById('showCreateTagBtn');
    const createTagModal = document.getElementById('create-tag-modal');
    const closeCreateTagModalBtn = document.getElementById('close-create-tag-modal');
    const createTagForm = document.getElementById('create-tag-form');
    const tagModal = document.getElementById('tag-modal');
    const closeTagModalBtn = document.getElementById('close-tag-modal');
    const addTagForm = document.getElementById('add-tag-form');
    const newTagInput = document.getElementById('new-tag-input');
    const existingTagsList = document.getElementById('existing-tags-list');
    const publicNotesSection = document.getElementById('public-notes-section');
    
    let debounceTimer;


    let state = {
        accessToken: localStorage.getItem('accessToken'),
        refreshToken: localStorage.getItem('refreshToken'),
        userNotes: [],
        publicNotes: [],
        tagSuggestions: [],
        selectedNoteName: null,
        selectedPublicNote: null, // <-- ADDED: To store the selected public note
        currentView: 'public',
    };

    async function apiCall(endpoint, method = 'GET', body = null, isMultipart = false, isRetry = false) {
        const headers = {};
        if (!isMultipart && body) { headers['Content-Type'] = 'application/json'; }
        if (state.accessToken) {
            headers['Authorization'] = `Bearer ${state.accessToken}`;
        }
        const config = { method, headers, body: isMultipart ? body : JSON.stringify(body) };
        if (!body) delete config.body;
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
            if (!response.ok) {
                if (response.status === 401 && !isRetry) {
                    const refreshed = await refreshAccessToken();
                    if (refreshed) return apiCall(endpoint, method, body, isMultipart, true);
                    throw new Error('Session expired. Please log in again.');
                }
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                throw new Error(errorData.message || `Server responded with status: ${response.status}`);
            }
            if (response.status === 204) return null;
            const contentType = response.headers.get("content-type");
            if (contentType?.includes("application/json")) {
                const text = await response.text();
                try { return JSON.parse(text); } catch (e) {
                    console.error("Failed to parse JSON:", text);
                    throw new SyntaxError("Invalid JSON received from server.");
                }
            }
            if (contentType?.includes("application/zip")) return response.blob();
            return null;
        } catch (error) {
            if (error.message !== 'Session expired. Please log in again.') showMessage(error.message, 'error');
            throw error;
        }
    }

    async function refreshAccessToken() {
        if (!state.refreshToken) {
            console.error("No refresh token available.");
            return false;
        }
        try {
            const response = await fetch(`${API_BASE_URL}/v1/auth/refresh-token`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${state.refreshToken}` }
            });
            if (!response.ok) throw new Error('Failed to refresh token.');
            const data = await response.json();
            updateAuthState(data.accessToken, data.refreshToken);
            return true;
        } catch (error) {
            updateAuthState(null, null);
            render();
            showMessage('Your session has expired. Please log in again.', 'error');
            return false;
        }
    }

    async function fetchUserNotes(query = '') {
        try {
            const endpoint = query
                ? `/v1/notes/search/name?name=${encodeURIComponent(query)}&value=0`
                : '/v1/notes/search?value=0';
            const notesData = await apiCall(endpoint);
            state.userNotes = notesData.content || [];
        } catch (error) {
            console.error("Failed to fetch user notes:", error);
            showMessage('Could not reload your notes.', 'error');
            state.userNotes = [];
        }
        renderUserNotesList();
    }

    async function fetchPublicNotes(query = '', searchType = 'name') {
        try {
            let endpoint;
            if (!query) {
                endpoint = '/v1/notes/search/public?page=0';
            } else if (searchType === 'name') {
                endpoint = `/v1/notes/search/public/name?name=${encodeURIComponent(query)}&page=0`;
            } else if (searchType === 'tag') {
                endpoint = `/v1/notes/search/public/tag?tag=${encodeURIComponent(query)}&page=0`;
            } else {
                endpoint = '/v1/notes/search/public?page=0';
            }
            
            const notesData = await apiCall(endpoint);
            state.publicNotes = notesData.content || notesData || [];
        } catch (error) {
            console.error("Failed to fetch public notes:", error);
            showMessage('Could not fetch public notes. Please check backend endpoints.', 'error');
            state.publicNotes = [];
        }
        renderPublicNotesList();
    }

    async function fetchTagSuggestions(query) {
        if (query.length < 1) { 
            state.tagSuggestions = [];
            renderTagSuggestions();
            return;
        }
        try {
            const data = await apiCall(`/v1/tag/search/name?name=${encodeURIComponent(query)}&page=0`);
            state.tagSuggestions = data.content || [];
            renderTagSuggestions();
        } catch (error) {
            console.error("Failed to fetch tag suggestions:", error);
            state.tagSuggestions = [];
            renderTagSuggestions();
        }
    }


    async function initializeApp() {
        if (state.accessToken) {
            try {
                const [user] = await Promise.all([
                    apiCall('/v1/user/me'),
                    fetchUserNotes(),
                    fetchPublicNotes(),
                ]);
                usernameDisplay.textContent = `Welcome, ${user.name || user.username}`;
            } catch (error) {
                updateAuthState(null, null);
            }
        } else {
             await fetchPublicNotes();
        }
        render();
    }

    function updateAuthState(accessToken, refreshToken) {
        state.accessToken = accessToken;
        state.refreshToken = refreshToken;
        if (accessToken) {
            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);
        } else {
            localStorage.clear();
            state = { ...state, userNotes: [], publicNotes: [], selectedNoteName: null, currentView: 'public' };
        }
    }

    function render() {
        toggleViews();
        if (state.accessToken) {
            renderUserNotesList();
        }
        renderRightColumn();
    }

    function toggleViews() {
        const loggedIn = !!state.accessToken;
        authView.classList.toggle('hidden', loggedIn);
        mainView.classList.toggle('hidden', !loggedIn);
        userInfo.classList.toggle('hidden', !loggedIn);
        if(loggedIn) {
            userInfo.classList.add('flex');
        } else {
            userInfo.classList.remove('flex');
        }
    }

    function renderUserNotesList() {
        if (mainView.classList.contains('hidden')) return;
        notesListEl.innerHTML = '';
        if (state.userNotes.length === 0) {
            notesListEl.innerHTML = `<p class="text-gray-400 p-2">No notes found.</p>`;
            return;
        }
        state.userNotes.forEach(note => {
            const noteElement = document.createElement('div');
            noteElement.className = `note-item p-3 rounded-md cursor-pointer hover:bg-gray-700/50 flex justify-between items-center`;
            noteElement.addEventListener('click', () => selectNote(note.name));

            noteElement.innerHTML = `
                <div>
                    <h3 class="font-semibold text-white truncate">${note.name}</h3>
                    <p class="text-sm text-gray-400 truncate">${note.description || 'No description'}</p>
                </div>
            `;

            if (note.name === state.selectedNoteName) {
                noteElement.classList.add('active');
                const icon = document.createElement('i');
                icon.className = 'fa-solid fa-pen-to-square ml-2 text-white';
                noteElement.appendChild(icon);
            }
            
            notesListEl.appendChild(noteElement);
        });
    }

    function formatDateTime(dateTimeString) {
        if (!dateTimeString) return 'N/A';
        const date = new Date(dateTimeString);
        if (isNaN(date.getTime())) return 'Invalid Date';
        const pad = (num) => num.toString().padStart(2, '0');
        const day = pad(date.getDate());
        const month = pad(date.getMonth() + 1);
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
    }

    function renderPublicNotesList() {
        publicNotesList.innerHTML = '';
        publicNotesList.className = 'space-y-4'; 

        if (state.publicNotes.length === 0) {
            publicNotesList.innerHTML = `<div class="text-center py-8"><p class="text-gray-400">No public notes found.</p></div>`;
            return;
        }

        state.publicNotes.forEach(note => {
            const card = document.createElement('div');
            card.className = 'public-note-list-item cursor-pointer';
            
            // --- MODIFIED: Click listener now fetches details ---
            card.addEventListener('click', () => {
                selectPublicNote(note.name);
            });

            const hasAnnotation = note.annotation && note.annotation.length > 0;

            card.innerHTML = `
                <div class="image-wrapper">
                    ${note.image ? `<img src="${note.image.objectUrl}" alt="${note.name}" onerror="this.style.display='none'; this.parentElement.innerHTML='<i class=\\'fa-regular fa-file-image icon-placeholder\\'></i>'">` : '<i class="fa-regular fa-file-image icon-placeholder"></i>'}
                </div>
                <div class="content-wrapper">
                    <div class="note-header">
                        <h3 class="note-title truncate">${note.name}</h3>
                        <div class="note-icons" title="${hasAnnotation ? 'Has annotation' : 'No annotation'}">
                            ${hasAnnotation ? '<i class="fa-solid fa-file-alt"></i>' : '<i class="fa-regular fa-file"></i>'}
                        </div>
                    </div>
                    <p class="note-description truncate">${note.description || 'No description provided.'}</p>
                    <div class="note-footer">
                        <span>Updated: ${formatDateTime(note.updatedAt)}</span>
                        <span>Created: ${formatDateTime(note.createdAt)}</span>
                    </div>
                </div>
            `;
            publicNotesList.appendChild(card);
        });
    }


    function renderRightColumn() {
        rightColumn.innerHTML = '';
        
        if (publicNotesSection.parentElement !== rightColumn) {
            publicNotesSection.remove();
        }

        if (state.currentView === 'create' && state.accessToken) {
            rightColumn.appendChild(createNoteTemplate.content.cloneNode(true));
            publicNotesSection.classList.add('hidden');
            attachCreateNoteFormListener();
        } else if (state.currentView === 'edit' && state.accessToken) {
            rightColumn.appendChild(editNoteTemplate.content.cloneNode(true));
            publicNotesSection.classList.add('hidden');
            populateAndAttachEditFormListener();
        } else if (state.currentView === 'detail' && state.selectedNoteName && state.accessToken) {
            renderNoteDetail();
            publicNotesSection.classList.add('hidden');
        // --- ADDED: Logic to show public note detail view ---
        } else if (state.currentView === 'public-detail' && state.selectedPublicNote) {
            renderPublicNoteDetail();
            publicNotesSection.classList.add('hidden');
        } else {
            publicNotesSection.classList.remove('hidden');
            rightColumn.appendChild(publicNotesSection);
            renderPublicNotesList();
        }
    }

    // --- ADDED: New function to render public note details ---
    function renderPublicNoteDetail() {
        const note = state.selectedPublicNote;
        if (!note) {
            state.currentView = 'public';
            render();
            return;
        }

        const tagsHtml = note.tags.map(tag => `<span class="tag-display-item">${tag.name}</span>`).join('');
        const attachmentsHtml = note.attachments.map(a => `<div class="flex justify-between items-center text-sm p-2 bg-gray-800/50 rounded-md"><span>${a.name}</span></div>`).join('');

        rightColumn.innerHTML = `
            <div class="mb-4">
                <button id="backToHomeBtn" class="btn btn-secondary text-sm"><i class="fa-solid fa-arrow-left mr-2"></i>Back</button>
            </div>
            <div class="flex justify-between items-start">
                <h2 class="text-2xl font-bold mb-2 text-white break-all">${note.name}</h2>
            </div>
            <p class="text-gray-400 mb-4">${note.description || ''}</p>
            <p class="text-gray-400 mb-4">Privacy: Public</p>
            <div class="tags-display-container">
                <h4 class="font-semibold text-white mb-2">Tags</h4>
                ${note.tags && note.tags.length > 0 ? tagsHtml : '<p class="text-sm text-gray-500">No tags found.</p>'}
            </div>
            <div class="prose max-w-none bg-gray-900/50 p-4 rounded-md mt-4">
                <h4 class="font-semibold text-white">Annotation</h4>
                <p class="text-gray-300">${note.annotation ? note.annotation.replace(/\n/g, '<br>') : 'N/A'}</p>
            </div>

            ${note.image ? `
            <div class="mt-6 border-t border-gray-700 pt-6">
                <h3 class="text-lg font-semibold mb-4 text-white">Image</h3>
                <img src="${note.image.objectUrl}" onerror="this.style.display='none'" alt="Note Image" class="w-full h-auto max-h-96 object-cover rounded-lg">
            </div>` : ''}

            <div class="mt-6 border-t border-gray-700 pt-6">
                <h3 class="text-lg font-semibold mb-4 text-white">Attachments</h3>
                <div class="space-y-2 mb-4">${attachmentsHtml || '<p class="text-sm text-gray-500">No attachments found.</p>'}</div>
                ${note.attachments && note.attachments.length > 0 ? `<button id="downloadPublicAttachmentsBtn" data-note-name="${note.name}" class="btn btn-secondary text-sm px-4 py-2">Download All (.zip)</button>` : ''}
            </div>`;
        
        document.getElementById('backToHomeBtn')?.addEventListener('click', () => {
            state.currentView = 'public';
            state.selectedPublicNote = null;
            render();
        });
    }
    
    function renderNoteDetail() {
        const note = state.userNotes.find(n => n.name === state.selectedNoteName);
        if (!note) {
            state.currentView = 'public';
            renderRightColumn();
            return;
        }

        const tagsHtml = note.tags.map(tag => `
            <span class="tag-display-item">
                ${tag.name}
                <button class="tag-display-remove-btn" data-tag-name="${tag.name}">&times;</button>
            </span>
        `).join('');

        rightColumn.innerHTML = `
            <div class="mb-4">
                <button id="backToHomeBtn" class="btn btn-secondary text-sm"><i class="fa-solid fa-arrow-left mr-2"></i>Back</button>
            </div>
            <div class="flex justify-between items-start">
                <h2 class="text-2xl font-bold mb-2 text-white break-all">${note.name}</h2>
                <div class="flex space-x-2">
                    <button data-note-name-edit="${note.name}" class="btn btn-edit px-3 py-1.5 text-sm">Edit</button>
                    <button data-note-name-delete="${note.name}" class="btn btn-danger px-3 py-1.5 text-sm">Delete</button>
                </div>
            </div>
            <p class="text-gray-400 mb-4">${note.description || ''}</p>
            <p class="text-gray-400 mb-4">Privacy: ${note.notePrivated ? 'Public' : 'Private'}</p>
            <div class="tags-display-container">
                <h4 class="font-semibold text-white mb-2">Tags</h4>
                ${note.tags && note.tags.length > 0 ? tagsHtml : '<p class="text-sm text-gray-500">No tags found.</p>'}
            </div>
            <div class="prose max-w-none bg-gray-900/50 p-4 rounded-md mt-4">
                <h4 class="font-semibold text-white">Annotation</h4>
                <p class="text-gray-300">${note.annotation ? note.annotation.replace(/\n/g, '<br>') : 'N/A'}</p>
            </div>

            ${note.image ? `
            <div class="mt-6 border-t border-gray-700 pt-6">
                <h3 class="text-lg font-semibold mb-4 text-white">Image</h3>
                <img src="${note.image.objectUrl}" onerror="this.style.display='none'" alt="Note Image" class="w-full h-auto max-h-96 object-cover rounded-lg">
            </div>` : ''}

            <div class="mt-6 border-t border-gray-700 pt-6">
                <h3 class="text-lg font-semibold mb-4 text-white">Attachments</h3>
                <div id="attachmentsList" class="space-y-2 mb-4">${note.attachments.map(a => `<div class="flex justify-between items-center text-sm p-2 bg-gray-800/50 rounded-md"><span>${a.name}</span> <button data-note-name-att-delete="${note.name}" data-attachment-name="${a.name}" class="text-red-400 hover:text-red-300 text-xs">Delete</button></div>`).join('') || '<p class="text-sm text-gray-500">No attachments found.</p>'}</div>
                ${note.attachments && note.attachments.length > 0 ? `<button id="downloadAttachmentsBtn" class="btn btn-secondary text-sm px-4 py-2">Download All (.zip)</button>` : ''}
                <form id="uploadAttachmentForm" class="mt-4"><input type="file" id="attachmentFile" multiple class="w-full text-sm text-gray-400 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:font-semibold file:bg-gray-700 file:text-gray-300 hover:file:bg-gray-600" required><button type="submit" class="btn btn-secondary px-4 py-2 rounded-md mt-2">Upload File</button></form>
            </div>`;
         attachNoteDetailListeners();
    }
    
    function renderTagSuggestions() {
        const container = document.getElementById('tagSuggestions');
        if (!container) return;
        container.innerHTML = '';
        if (state.tagSuggestions.length === 0) {
            container.classList.add('hidden');
            return;
        }
        container.classList.remove('hidden');
        state.tagSuggestions.forEach(tag => {
            const item = document.createElement('div');
            item.className = 'tag-suggestion-item';
            item.textContent = tag.name;
            item.addEventListener('click', () => {
                const tagInput = document.getElementById('noteTags');
                const currentTags = tagInput.value.split(',').map(t => t.trim()).filter(Boolean);
                currentTags.pop(); 
                if (!currentTags.includes(tag.name)) {
                    currentTags.push(tag.name); 
                }
                tagInput.value = currentTags.join(', ') + ', '; 
                state.tagSuggestions = [];
                renderTagSuggestions(); 
                tagInput.focus();
            });
            container.appendChild(item);
        });
    }


    function setupTextareaWithCounter(textareaId, counterId, limit) {
        const textarea = document.getElementById(textareaId);
        const counter = document.getElementById(counterId);
        if (!textarea || !counter) return;

        const update = () => {
            let currentLength = textarea.value.length;
            if (currentLength > limit) {
                textarea.value = textarea.value.substring(0, limit);
                currentLength = limit;
            }
            counter.textContent = `${currentLength} / ${limit}`;

            textarea.style.height = 'auto';
            textarea.style.height = `${textarea.scrollHeight}px`;
        };
        
        textarea.addEventListener('input', update);
        textarea.addEventListener('focus', update);
        
        update();
    }

    function attachAuthFormListeners() {
        document.getElementById('signInForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = e.target.querySelector('button');
            btn.disabled = true; btn.textContent = 'Signing In...';
            try {
                const data = await apiCall('/v1/auth/sign-in', 'POST', {
                    userinfo: document.getElementById('signInIdentifier').value,
                    password: document.getElementById('signInPassword').value,
                });
                updateAuthState(data.accessToken, data.refreshToken);
                await initializeApp();
                showMessage('Login successful! Loading data...', 'success');
            } catch (error) { console.error("An error occurred after login:", error); } 
            finally { btn.disabled = false; btn.textContent = 'Sign In'; }
        });
        document.getElementById('signUpForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = e.target.querySelector('button');
            btn.disabled = true; btn.textContent = 'Creating Account...';
            const password = document.getElementById('signUpPassword').value;
            const confirmPassword = document.getElementById('signUpConfirmPassword').value;
            if (password !== confirmPassword) {
                showMessage('Passwords do not match.', 'error');
                btn.disabled = false; btn.textContent = 'Create Account';
                return;
            }
            try {
                const data = await apiCall('/v1/auth/sign-up', 'POST', {
                    name: document.getElementById('signUpName').value,
                    username: document.getElementById('signUpUsername').value,
                    email: document.getElementById('signUpEmail').value,
                    password: password,
                    confirmpassword: confirmPassword
                });
                updateAuthState(data.accessToken, data.refreshToken);
                await initializeApp();
                showMessage('Account created! Loading data...', 'success');
            } catch (error) { console.error("An error occurred after sign-up:", error); } 
            finally { btn.disabled = false; btn.textContent = 'Create Account'; }
        });
        showSignUpBtn.addEventListener('click', () => { signInCard.classList.add('hidden'); signUpCard.classList.remove('hidden'); });
        showSignInBtn.addEventListener('click', () => { signUpCard.classList.add('hidden'); signInCard.classList.remove('hidden'); });
    }

    function attachCreateNoteFormListener() {
        const tagInput = document.getElementById('noteTags');
        const suggestionsContainer = document.getElementById('tagSuggestions');
        const noteImageInput = document.getElementById('noteImage');
        const noteImagePreviewContainer = document.getElementById('noteImagePreview');
        const imagePreview = document.getElementById('imagePreview');
        const removeImagePreviewBtn = document.getElementById('removeImagePreview');

        noteImageInput.addEventListener('change', () => {
            const file = noteImageInput.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    imagePreview.src = e.target.result;
                    noteImagePreviewContainer.classList.remove('hidden');
                };
                reader.readAsDataURL(file);
            }
        });

        removeImagePreviewBtn.addEventListener('click', () => {
            noteImageInput.value = ''; // Clear the file input
            imagePreview.src = '';
            noteImagePreviewContainer.classList.add('hidden');
        });

        if (tagInput) {
            tagInput.addEventListener('input', () => {
                clearTimeout(debounceTimer);
                const tags = tagInput.value.split(',');
                const currentTag = tags[tags.length - 1].trim();
                debounceTimer = setTimeout(() => {
                    fetchTagSuggestions(currentTag);
                }, 300); 
            });
            tagInput.addEventListener('blur', () => {
                setTimeout(() => {
                    if (!suggestionsContainer.matches(':hover')) {
                       suggestionsContainer.classList.add('hidden');
                    }
                }, 200);
            });
            tagInput.addEventListener('focus', () => {
                 const tags = tagInput.value.split(',');
                 const currentTag = tags[tags.length - 1].trim();
                 if(currentTag) {
                    fetchTagSuggestions(currentTag);
                 }
            });
        }
        
        document.getElementById('cancelCreateBtn').addEventListener('click', () => {
            state.currentView = 'public';
            state.selectedNoteName = null;
            render();
        });

        document.getElementById('createNoteForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = e.target.querySelector('button[type="submit"]');
            btn.disabled = true; btn.textContent = 'Saving...';
            const noteTitle = document.getElementById('noteTitle').value;
            try {
                const newNoteResponse = await apiCall('/v1/notes/create', 'POST', {
                    name: noteTitle,
                    description: document.getElementById('noteDescription').value,
                    annotation: document.getElementById('noteAnnotation').value,
                    tags: document.getElementById('noteTags').value.split(',').map(t => t.trim()).filter(Boolean),
                    notePrivated: document.getElementById('notePublic').checked
                });
                if (!newNoteResponse) { throw new Error("Note creation failed: server returned no response."); }
                const imageFile = document.getElementById('noteImage').files[0];
                if (imageFile) {
                    const imageFormData = new FormData();
                    imageFormData.append('noteName', noteTitle);
                    imageFormData.append('file', imageFile);
                    await apiCall('/v1/image/upload', 'POST', imageFormData, true);
                }
                const attachmentFiles = document.getElementById('noteAttachments').files;
                if (attachmentFiles.length > 0) {
                    showMessage(`Uploading ${attachmentFiles.length} attachments...`, 'info');
                    for (const file of attachmentFiles) {
                        const attachmentFormData = new FormData();
                        attachmentFormData.append('name', noteTitle);
                        attachmentFormData.append('file', file);
                        await apiCall('/v1/attachment/upload', 'POST', attachmentFormData, true);
                    }
                }
                showMessage('Note created successfully!', 'success');
                await fetchUserNotes();
                selectNote(noteTitle);
            } catch(e) { console.error("Error during note creation process:", e); } 
            finally { btn.disabled = false; btn.textContent = 'Save Note'; }
        });
        setupTextareaWithCounter('noteAnnotation', 'createAnnotationCounter', 50000);
    }

    function populateAndAttachEditFormListener() {
        const note = state.userNotes.find(n => n.name === state.selectedNoteName);
        if (!note) return;
        const form = document.getElementById('editNoteForm');

        const editNoteImageInput = form.querySelector('#editNoteImage');
        const editNoteImagePreviewContainer = form.querySelector('#editNoteImagePreview');
        const editImagePreview = form.querySelector('#editImagePreview');
        const removeEditImagePreviewBtn = form.querySelector('#removeEditImagePreview');
        
        form.querySelector('#editNoteTitle').value = note.name;
        form.querySelector('#editNoteDescription').value = note.description || '';
        form.querySelector('#editNoteAnnotation').value = note.annotation || '';
        form.querySelector('#editNotePublic').checked = note.notePrivated;
        form.querySelector('#manage-tags-btn').addEventListener('click', openTagModal);

        if (note.image && note.image.objectUrl) {
            editImagePreview.src = note.image.objectUrl;
            editNoteImagePreviewContainer.classList.remove('hidden');
        }

        editNoteImageInput.addEventListener('change', () => {
            const file = editNoteImageInput.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    editImagePreview.src = e.target.result;
                    editNoteImagePreviewContainer.classList.remove('hidden');
                };
                reader.readAsDataURL(file);
            }
        });

        removeEditImagePreviewBtn.addEventListener('click', () => {
            editNoteImageInput.value = '';
            editImagePreview.src = '';
            editNoteImagePreviewContainer.classList.add('hidden');
        });
        
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = e.target.querySelector('button[type="submit"]');
            btn.disabled = true; btn.textContent = 'Saving...';
            try {
                const updatedData = {
                    description: form.querySelector('#editNoteDescription').value,
                    annotation: form.querySelector('#editNoteAnnotation').value,
                    notePrivated: form.querySelector('#editNotePublic').checked
                };
                await apiCall(`/v1/notes/edit/${encodeURIComponent(note.name)}`, 'PUT', updatedData);

                const newImageFile = editNoteImageInput.files[0];
                if (newImageFile) {
                    showMessage('Uploading new image...', 'info');
                    const imageFormData = new FormData();
                    imageFormData.append('noteName', note.name);
                    imageFormData.append('file', newImageFile);
                    await apiCall('/v1/image/upload', 'POST', imageFormData, true);
                }

                showMessage('Note updated successfully!', 'success');
                await fetchUserNotes();
                selectNote(note.name);
            } catch (err) { console.error("Error updating note:", err); } 
            finally { btn.disabled = false; btn.textContent = 'Save Changes'; }
        });
        document.getElementById('cancelEditBtn').addEventListener('click', () => {
            selectNote(state.selectedNoteName);
        });
        setupTextareaWithCounter('editNoteAnnotation', 'editAnnotationCounter', 50000);
    }

    function attachNoteDetailListeners() {
        document.getElementById('backToHomeBtn')?.addEventListener('click', () => {
            state.currentView = 'public';
            state.selectedNoteName = null;
            render();
        });

        const tagContainer = rightColumn.querySelector('.tags-display-container');
        if (tagContainer) {
            tagContainer.addEventListener('click', async (e) => {
                if (e.target.matches('.tag-display-remove-btn')) {
                    const tagName = e.target.dataset.tagName;
                    if (!tagName || !state.selectedNoteName) return;

                    try {
                        await apiCall('/v1/notes/attach/remove', 'DELETE', { noteName: state.selectedNoteName, tagName: tagName });
                        showMessage(`Tag "${tagName}" removed.`, 'success');
                        await selectNote(state.selectedNoteName);
                    } catch (error) {
                        console.error("Failed to remove tag:", error);
                    }
                }
            });
        }

        document.getElementById('uploadAttachmentForm')?.addEventListener('submit', async (e) => {
            e.preventDefault();
            const files = e.target.querySelector('#attachmentFile').files;
            if (files.length === 0 || !state.selectedNoteName) return;
    
            const btn = e.target.querySelector('button[type="submit"]');
            btn.disabled = true;
            btn.textContent = `Uploading ${files.length} file(s)...`;
            
            try {
                for (const file of files) {
                    const formData = new FormData();
                    formData.append('name', state.selectedNoteName);
                    formData.append('file', file);
                    await apiCall('/v1/attachment/upload', 'POST', formData, true);
                }
                showMessage(`${files.length} attachment(s) uploaded successfully!`, 'success');
                e.target.reset(); 
                await selectNote(state.selectedNoteName);
            } catch(e) { 
                 console.error("Failed to upload attachments:", e);
            }
            finally { 
                btn.disabled = false; 
                btn.textContent = 'Upload File';
            }
        });
        document.querySelector('[data-note-name-edit]')?.addEventListener('click', () => {
            state.currentView = 'edit';
            render();
        });
    }

    // --- ADDED: New function to select a public note and fetch its details ---
    async function selectPublicNote(noteName) {
        state.currentView = 'public-detail';
        state.selectedPublicNote = null; // Clear previous
        render(); // Show loading state if any
        try {
            const noteData = await apiCall(`/v1/notes/search/public/name?name=${encodeURIComponent(noteName)}&page=0`);
            const fullNote = noteData?.content?.[0];
            if (fullNote) {
                state.selectedPublicNote = fullNote;
            } else {
                showMessage(`Could not find details for note: ${noteName}`, 'error');
                state.currentView = 'public';
            }
        } catch (error) {
            showMessage('Could not load note details.', 'error');
            state.currentView = 'public';
        } finally {
            render(); // Re-render with fetched data or back to public list
        }
    }
    
    async function selectNote(noteName) {
        state.selectedNoteName = noteName;
        state.currentView = 'detail';
        render(); // Render immediately to show loading state if needed
        try {
            const noteData = await apiCall(`/v1/notes/search/name?name=${encodeURIComponent(noteName)}&value=0`);
            const freshNote = noteData?.content?.[0];
            if (freshNote) {
                const noteIndex = state.userNotes.findIndex(n => n.name === noteName);
                if (noteIndex !== -1) {
                    state.userNotes[noteIndex] = freshNote;
                } else {
                    state.userNotes.unshift(freshNote);
                }
            }
             render(); // Re-render with the full note details
        } catch (error) {
            showMessage('Could not refresh note details.', 'error');
        }
       
    }
    
    function openTagModal() {
        const note = state.userNotes.find(n => n.name === state.selectedNoteName);
        if (!note) return;
        renderTagList(note.tags);
        tagModal.classList.remove('hidden');
    }

    function renderTagList(tags = []) {
        existingTagsList.innerHTML = '';
        if (tags.length === 0) {
            existingTagsList.innerHTML = `<p class="text-gray-400 text-sm">No tags attached.</p>`;
            return;
        }
        tags.forEach(tag => {
            const tagEl = document.createElement('div');
            tagEl.className = 'tag-item';
            tagEl.innerHTML = `
                <span>${tag.name}</span>
                <button data-tag-name="${tag.name}" class="tag-remove-btn">&times;</button>
            `;
            existingTagsList.appendChild(tagEl);
        });
    }

    // --- EVENT LISTENERS ---

    logoutButton.addEventListener('click', async () => {
        try { await apiCall('/v1/user/logout', 'PUT'); }
        catch(e) { console.error("Logout failed but continuing.");}
        finally {
           updateAuthState(null, null);
           render();
           showMessage('You have been logged out.', 'success');
        }
    });

    showCreateNoteBtn.addEventListener('click', () => {
        state.currentView = 'create';
        state.selectedNoteName = null;
        render();
    });

    homeLink.addEventListener('click', () => {
        state.currentView = 'public';
        state.selectedNoteName = null;
        state.selectedPublicNote = null;
        render();
    });

    publicSearchForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const query = publicSearchInput.value.trim();
        const searchType = document.getElementById('public-search-type').value;
        fetchPublicNotes(query, searchType);
    });

    publicSearchInput.addEventListener('input', (e) => {
        if (e.target.value === '') {
            fetchPublicNotes();
        }
    });

    // Tag Management Event Listeners
    showCreateTagBtn.addEventListener('click', () => createTagModal.classList.remove('hidden'));
    closeCreateTagModalBtn.addEventListener('click', () => createTagModal.classList.add('hidden'));
    createTagForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = e.target.querySelector('button[type="submit"]');
        const input = document.getElementById('create-new-tag-input');
        const tagName = input.value.trim();
        if(!tagName) return;

        btn.disabled = true;
        btn.textContent = 'Creating...';

        try {
            await apiCall('/v1/tag/new', 'POST', { name: tagName });
            showMessage(`Tag "${tagName}" created.`, 'success');
            input.value = '';
            createTagModal.classList.add('hidden');
        } catch(err) {
            console.error("Failed to create tag:", err);
        } finally {
            btn.disabled = false;
            btn.textContent = 'Create';
        }
    });

    closeTagModalBtn.addEventListener('click', () => tagModal.classList.add('hidden'));
    addTagForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const tagName = newTagInput.value.trim();
        if (!tagName || !state.selectedNoteName) return;
        try {
            await apiCall('/v1/notes/attach/note', 'POST', { noteName: state.selectedNoteName, tagName: tagName });
            newTagInput.value = '';
            await selectNote(state.selectedNoteName);
            const note = state.userNotes.find(n => n.name === state.selectedNoteName);
            renderTagList(note.tags);
        } catch (error) {
            console.error("Failed to add tag:", error);
        }
    });
    existingTagsList.addEventListener('click', async (e) => {
        if (e.target.matches('.tag-remove-btn')) {
            const tagName = e.target.dataset.tagName;
            if (!tagName || !state.selectedNoteName) return;
            try {
                 await apiCall('/v1/notes/attach/remove', 'DELETE', { noteName: state.selectedNoteName, tagName: tagName });
                 await selectNote(state.selectedNoteName);
                 const note = state.userNotes.find(n => n.name === state.selectedNoteName);
                 renderTagList(note.tags);
            } catch (error) {
                console.error("Failed to remove tag:", error)
            }
        }
    });

    document.body.addEventListener('click', async (e) => {
        // --- MODIFIED: Split download button logic for private vs public notes ---
        const handleDownload = async (noteName) => {
            const btn = e.target;
            const originalBtnContent = btn.innerHTML;
            btn.disabled = true;
            btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin mr-2"></i>Downloading...`;
        
            try {
                const blob = await apiCall(`/v1/attachment/download?name=${encodeURIComponent(noteName)}`);
                if (blob && blob.size > 0) {
                     const a = document.createElement('a');
                     a.href = window.URL.createObjectURL(blob);
                     a.download = `${noteName}_attachments.zip`;
                     document.body.appendChild(a);
                     a.click();
                     a.remove();
                     window.URL.revokeObjectURL(a.href);
                } else {
                    showMessage('No attachments to download or an error occurred.', 'info');
                }
            } catch(error) {
                console.error("Download failed:", error);
            } finally {
                btn.disabled = false;
                btn.innerHTML = originalBtnContent;
            }
        };

        if (e.target.dataset.noteNameDelete) {
            if (confirm(`Are you sure you want to delete the note "${e.target.dataset.noteNameDelete}"?`)) {
                await apiCall(`/v1/notes/delete?name=${encodeURIComponent(e.target.dataset.noteNameDelete)}`, 'DELETE');
                showMessage('Note deleted.', 'success');
                state.selectedNoteName = null;
                state.currentView = 'public';
                await fetchUserNotes();
                render();
            }
        }
        else if (e.target.dataset.noteNameAttDelete && e.target.dataset.attachmentName) {
             const deleteAttNoteName = e.target.dataset.noteNameAttDelete;
             const deleteAttName = e.target.dataset.attachmentName;
             if(confirm(`Are you sure you want to delete the attachment "${deleteAttName}"?`)){
                 await apiCall(`/v1/attachment/delete?noteName=${encodeURIComponent(deleteAttNoteName)}&attachmentName=${encodeURIComponent(deleteAttName)}`, 'DELETE');
                 showMessage('Attachment deleted.', 'success');
                 await selectNote(deleteAttNoteName); 
             }
        }
        else if (e.target.id === 'downloadAttachmentsBtn' && state.selectedNoteName) {
            await handleDownload(state.selectedNoteName);
        } else if (e.target.id === 'downloadPublicAttachmentsBtn') {
            const noteName = e.target.dataset.noteName;
            if (noteName) {
                await handleDownload(noteName);
            }
        }
    });

    function showMessage(message, type = 'info') {
        const messageBox = document.getElementById('messageBox');
        if(!messageBox) return;
        messageBox.textContent = message;
        messageBox.className = 'fixed bottom-5 right-5 p-4 rounded-lg text-white max-w-sm shadow-lg transform translate-x-full transition-transform duration-300 ease-out';
        if (type === 'success') messageBox.classList.add('bg-green-600');
        else if (type === 'error') messageBox.classList.add('bg-red-600');
        else messageBox.classList.add('bg-blue-600');
        setTimeout(() => {
            messageBox.classList.remove('translate-x-full');
        }, 100);
        setTimeout(() => {
            messageBox.classList.add('translate-x-full');
        }, 4000);
    }

    attachAuthFormListeners();
    initializeApp();
});