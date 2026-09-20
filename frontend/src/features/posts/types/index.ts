export type DashboardPost = {
  id: number;
  clubId: number;
  title: string;
  description: string;
  createdBy: {
    id: number;
    name: string;
    email: string;
  };
  lastUpdatedBy: {
    id: number;
    name: string;
    email: string;
  };
  createdAt: string;
  updatedAt: string;
};
