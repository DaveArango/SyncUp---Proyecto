export interface User {
  id: string;
  username: string;
  name: string;
  isActive: boolean;
  role: 'user' | 'admin';
}
