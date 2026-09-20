export type Role = 'MEMBER' | 'ADMIN';

export const permissions = {
  editClub: (role: Role) => role === 'ADMIN',
  deleteClub: (role: Role) => role === 'ADMIN',
  manageMembers: (role: Role) => role === 'ADMIN',
  createPost: (role: Role) => role === 'ADMIN',
};
