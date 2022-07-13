#!/usr/bin/perl -w

# Juha Kuikka <juha.kuikka@synapse.com>
# Version 1
# Script for parsing TAP reader output in the form of:
# 2011-12-06T08:13:18.487+0000,TAP,Monitor57,8002DE89B5BD04
# 
# Notes:
# - Multiple consequent reads from single location are ignored, only last is used
# - Two load readers are handled as one
# - "return times" (from Load -> Entry, only seen in testing) are not shown

use Date::Parse;
use Data::Dumper;

# %taps is a hash in the format of:
# '8002DE8974BD04' => {                   <-- Band ID (RFID)
#     'reader' => 'Load',                 <-- Location where it was last seen (see map_name)
#     'ts' => '1323159488.514'            <-- Timestamp (seconds)
# },
# '8002DE89FABC04' => {
#     'reader' => 'Load',
#     'ts' => '1323159489.673'
# },
#
my %taps = ();

select(STDOUT);
$|++; # Enable autoflush on stdout

# This function maps reader name to location,
# change accordingly to your configuration
sub map_name
{
	my $reader = shift;
	if($reader =~ /Monitor50/) {
		return "Entry";
	}
	if($reader =~ /Monitor57/) {
		return "Merge";
	}
	if($reader =~ /Monitir52/) {
		return "Load";
	}
	if($reader =~ /Monitor06/) {
		return "Load";
	}
	return $reader;
}

while (<>)
{
	my @fields = split(/,/);

	if($fields[1] eq "TAP") {
		my $ts = str2time($fields[0]);
		my $reader = map_name($fields[2]);
		my $band = $fields[3];

		$band =~ s/\r\n//; # Remove MSDOS LF+CR
		chomp($band); # Remove trailing whitespace

		# seen this band before
		if(defined $taps{$band}{"reader"}) {
			my $prev_reader = map_name($taps{$band}{"reader"});

			# seen this band at a different named reader
			if($prev_reader ne "Load" && # So not show "Load -> XXXX"  lines
			   $prev_reader ne $reader) {
				my $prev_ts = $taps{$band}{"ts"};
				printf("%s Band %s: %s -> %s time %d seconds\n",
					$fields[0], $band, $prev_reader, 
					$reader, $ts - $prev_ts);
			}
		} else {
			# Have not seen this band before
			printf("%s First TAP %s on %s\n", $fields[0], $band, $reader);
		}
		$taps{$band}{"reader"} = $reader;
		$taps{$band}{"ts"} = $ts;
	}
}

# For debugging
# print Dumper(\%taps);

# vim: cindent shiftwidth=8 cinoptions=>1s,(0,t0 tabstop=8
