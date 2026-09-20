import type { Role } from '@/utils/permissions';

export type ClubMember = {
  id: number;
  name: string;
  email: string;
  role: Role;
  userId: number;
  joinedAt: number;
};
