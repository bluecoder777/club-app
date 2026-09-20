import { ClubList } from '../components/list/club-list';
import { ClubsHeader } from '../components/list/clubs-header';

export const ClubsRoute = () => {
  return (
    <div className="flex flex-col gap-8 px-8">
      <ClubsHeader />
      <ClubList />
    </div>
  );
};
