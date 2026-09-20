import { CreateClub } from '../actions/create-club';

export const ClubsHeader = () => {
  return (
    <div className="flex justify-between gap-8">
      <div className="flex flex-col">
        <h1 className="text-2xl font-bold tracking-tight">Clubs</h1>
        <p className="text-muted-foreground text-sm">
          Discover and join clubs that match your interests.
        </p>
      </div>
      <CreateClub />
    </div>
  );
};
