export async function apiFetch(url: string, options: RequestInit = {}): Promise<Response> {
    const headers = new Headers(options.headers);
    const method = (options.method || 'GET').toUpperCase();

    if (!['GET', 'HEAD', 'OPTIONS'].includes(method) && typeof document !== 'undefined') {
        
        let match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/);
        if (!match) {
            await fetch('/api/csrf', { credentials: 'include' });
            match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/);
        }
        
        if (match) {
            headers.set('X-XSRF-TOKEN', decodeURIComponent(match[1]));
        }
    }

    return fetch(url, {
        ...options,
        headers,
        credentials: 'include'
    });
}