export interface User {
  id: string;
  email: string;
  name: string;
  isActive: boolean;
  role: 'user' | 'admin';
}
