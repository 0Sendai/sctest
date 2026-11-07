import type { JwtPayload } from 'jsonwebtoken';

export interface UserPayload extends JwtPayload {
    id: number;
    username: string;
    role:     string;
} 

export function getUserFromToken(token: string): UserPayload | null {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) return null;
        
        const payloadJson = Buffer.from(parts[1], 'base64').toString('utf-8');
        const payload = JSON.parse(payloadJson);
        
        
        return {
            id: payload.id,
            username: payload.name,
            role: payload.role
        };
    } catch (e) {
        return null;
    }
}