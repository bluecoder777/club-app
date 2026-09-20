import { ArrowLeft, MessageSquare, Users } from 'lucide-react';
import { useNavigate, useParams } from 'react-router';
import ClubDetailsHeader from './club-details-header';
import { Posts } from '@/features/posts/components/list/posts';
import { Members } from '@/features/members/components/members';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

export default function ClubDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  if (!id) {
    return null;
  }

  const idAsNumber = Number(id);

  return (
    <div className="container ml-auto space-y-8 px-8">
      <Button onClick={() => navigate(-1)} variant="ghost">
        <ArrowLeft className="mr-2 size-4" />
        Back to Clubs
      </Button>

      <ClubDetailsHeader clubId={idAsNumber} />

      <Tabs defaultValue="posts" className="w-full">
        <TabsList className="bg-muted h-10 rounded-lg p-1">
          <TabsTrigger
            value="posts"
            className="data-[state=active]:bg-background gap-2 rounded-md px-4"
          >
            <MessageSquare className="size-4" />
            Posts
          </TabsTrigger>

          <TabsTrigger
            value="members"
            className="data-[state=active]:bg-background gap-2 rounded-md px-4"
          >
            <Users className="size-4" />
            Members
          </TabsTrigger>
        </TabsList>

        <TabsContent value="posts" className="mt-6">
          <Posts clubId={idAsNumber} />
        </TabsContent>

        <TabsContent value="members" className="mt-6">
          <Members clubId={idAsNumber} />
        </TabsContent>
      </Tabs>
    </div>
  );
}
