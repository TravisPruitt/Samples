#!/usr/bin/perl -w

use Data::Dumper;

my %bands = ();
my %bands_sng = ();
my $single_singulation = 0;
my %band_names = ();

if($0 =~ /sng-single/) {
	$single_singulation = 1;
}

select(STDOUT);
$|++; # Enable autoflush on stdout

while (<>)
{
	my @fields = split(/,/, $_);

	if($fields[1] eq "PROCESS") {
		%bands = ();
	}

	if($fields[1] eq "TAP") {
		my $reader = $fields[2];
		my $band = $fields[4];

		chomp($band);
		printf("TAP %s on %s\n", $band, $reader);
	}

	if($fields[1] eq "LRR") {
		my $reader = $fields[2];
		my $name = $fields[3];
		my $band = $fields[4];
		my $rssi = $fields[6];

		if(defined $bands{$band}{$reader}) {
			if($rssi > $bands{$band}{$reader}) {
				$bands{$band}{$reader} = $rssi;
			}
		} else {
			$bands{$band}{$reader} = $rssi;
		}
		if(!defined $band_names{$band}) {
			if($name !~ /null SG|LRID/) {
				$band_names{$band} = $name;
			}
		}
	}

	if($fields[1] eq "SNG") {
		my $where = $fields[2];
		my $band = $fields[3];
		my $prev_sng = $bands_sng{$band};

		if($single_singulation == 1 && defined $prev_sng && $prev_sng eq $where) {
			next;
		}
		$bands_sng{$band} = $where;
		
		if(defined $band_names{$band}) {
			printf("SNG %s(%s) in %s RSSI ", $band_names{$band}, $band, $where);
		} else {
			printf("SNG %s in %s RSSI ", $band, $where);
		}
		if($where eq "MergeLrr") {
			$where = "Merge";
		}
		foreach(("1", "2", "3", "4")) {
			if(defined $bands{$band}{$where . $_}) {
				print $bands{$band}{$where . $_} . ", ";
			} else {
				print "n/a, ";
			}
		}
		print("\n");
	}
}

# vim: cindent 
