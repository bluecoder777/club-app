export function ClubListLoading() {
  return (
    <div className="grid grid-cols-[repeat(auto-fit,minmax(260px,1fr))] gap-3">
      {Array.from({ length: 6 }).map((_, index) => (
        <div
          key={index}
          className="flex min-h-54 min-w-0 animate-pulse flex-col rounded-xl bg-white p-4"
        >
          <div className="h-10 w-10 shrink-0 rounded-lg bg-white/10" />

          <div className="mt-3 space-y-2">
            <div className="h-4 w-2/3 rounded bg-white/10" />
            <div className="h-3 w-full rounded bg-white/10" />
            <div className="h-3 w-4/5 rounded bg-white/10" />
          </div>

          <div className="mt-auto flex items-center justify-between pt-6">
            <div className="h-3 w-20 rounded bg-white/10" />
            <div className="h-8 w-16 rounded-md bg-white/10" />
          </div>
        </div>
      ))}
    </div>
  );
}
