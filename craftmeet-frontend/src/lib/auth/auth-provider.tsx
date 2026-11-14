'use client';

import {
    createContext,
    useCallback,
    useContext,
    useEffect,
    useState,
    type ReactNode,
} from 'react';
import * as authApi from '@/lib/api/auth';
import type { User } from '@/lib/types/user';

export type AuthStatus = 'loading' | 'authenticated' | 'unauthenticated';

type AuthContextValue = {
    user: User | null;
    status: AuthStatus;
    login: (email: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [status, setStatus] = useState<AuthStatus>('loading');

    // Restore the session on mount. A failed `me()` (no/invalid/expired token)
    // is treated as unauthenticated.
    useEffect(() => {
        let cancelled = false;
        authApi
            .me()
            .then((u) => {
                if (!cancelled) {
                    setUser(u);
                    setStatus('authenticated');
                }
            })
            .catch(() => {
                if (!cancelled) {
                    setUser(null);
                    setStatus('unauthenticated');
                }
            });
        return () => {
            cancelled = true;
        };
    }, []);

    const login = useCallback(async (email: string, password: string) => {
        const u = await authApi.login(email, password);
        setUser(u);
        setStatus('authenticated');
    }, []);

    const logout = useCallback(async () => {
        try {
            await authApi.logout();
        } catch {
            // clear local state even if the server call fails (e.g. no cookie)
        }
        setUser(null);
        setStatus('unauthenticated');
    }, []);

    return <AuthContext.Provider value={{ user, status, login, logout }}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within an AuthProvider');
    return ctx;
}
