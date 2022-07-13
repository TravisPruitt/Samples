#!/usr/bin/perl -w

use strict;

my %bands = ();
my $timeout = 15;
my $last_print = 0;

select(STDOUT);
$|++; # Enable autoflush on stdout

sub flush_bands()
{
	my $now = time();

	while ( my ($key, $value) = each(%bands) ) {
		if($now - $value > $timeout) {
			print "Deleting $key\n";
			delete $bands{$key};
		}
	}
}

sub print_bands()
{
	my $now = time();
	my $row = 2;
	my $col = 1;
	
	flush_bands();
	print("\033[2J");
	print("\033[H");

	#while ( my ($key, $value) = each(%bands) ) {
	for my $key ( sort keys %bands ) {
		my $value = $bands{$key};
#		print "band $key\n";
#		print "value $value\n";
		printf("\033[%d;%dH", $row, $col);
		print $key . " " . ($now - $value);
		$row++;
		if($row == 25) {
			$row = 2;
			$col += 25;
		}
	}
	print("\033[H");
	printf("Saw %d bands in the last %d seconds", scalar keys %bands, $timeout); 
}

while(<>)
{
	my @fields = split(/,/, $_);
	my $now = time();

	if($fields[1] eq "LRR") {
		my $reader = $fields[2];
		my $band = $fields[4];
		my $rssi = $fields[6];

		$bands{$band} = $now;
	}

	if($now - $last_print >= 1) {
		print_bands();
		$last_print = $now;
	}
}
