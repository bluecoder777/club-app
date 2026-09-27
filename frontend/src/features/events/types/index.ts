export type ClubEvent = {
  id: number;
  clubId: number;
  name: string;
  description: string;
  venue: string;
  eventTime: string;
  capacity: number;
  ticketsIssued: number;
  hasTicket: boolean;
};
