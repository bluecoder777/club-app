import type { Role } from '@/utils/permissions';

export type Club = {
  id: number;
  name: string;
  description: string;
  memberCount: number;
  dateOfCreation: number;
  createdBy: {
    id: number;
    name: string;
    email: string;
  };
  isMember: boolean;
  currentUserRole: Role | null;
};
